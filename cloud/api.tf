resource "aws_ecs_cluster" "api-cluster" {
  name = "api"
}

data "template_file" "api_task_definition" {
  template = file("${path.module}/templates/container_definition.tpl")
  vars = {
    api_repository = "${aws_ecr_repository.mealplanner-api.repository_url}:latest"
    aws_region = var.aws_region
    admin_username = var.secret_admin_username
    secret_key = var.secret_authSecretKey
    admin_password = var.secret_admin_password
    active_profile = var.active_profile
    log_group = aws_cloudwatch_log_group.api.name
  }
}

resource "aws_ecs_task_definition" "api-task" {
  family                   = "service"
  network_mode = "bridge"
  requires_compatibilities = ["EC2"] 

  container_definitions = data.template_file.api_task_definition.rendered
}

resource "aws_iam_role" "api-ecs-role" {
  name = "api-ecs-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_policy" "ecs-task-policy" {
  name        = "ecs-task-policy"
  description = "Policy to allow ECS tasks to access ECR"
  policy      = data.aws_iam_policy_document.ecs-task-policy.json
}

resource "aws_iam_role_policy_attachment" "ecs-task-policy-attachment" {
  policy_arn = aws_iam_policy.ecs-task-policy.arn
  role       = aws_iam_role.api-ecs-role.name
}

data "aws_iam_policy_document" "ecs-task-policy" {
  statement {
    actions   = ["ecr:GetAuthorizationToken", "ecr:BatchCheckLayerAvailability", "ecr:GetDownloadUrlForLayer", "ecr:GetRepositoryPolicy", "ecr:DescribeRepositories", "ecr:ListImages", "ecr:GetLifecyclePolicy", "ecr:GetLifecyclePolicyPreview", "ecr:GetImageScanFindings", "ecr:BatchGetImage"]
    resources = ["*"]
  }
}

resource "aws_ecs_service" "api-service" {
  name            = "api-service"
  cluster         = aws_ecs_cluster.api-cluster.id
  task_definition = aws_ecs_task_definition.api-task.arn
  desired_count   = 1
#   iam_role = aws_iam_role.ecs_service_role.arn
  launch_type     = "EC2" 

#   force_new_deployment = true
#    capacity_provider_strategy {
#    capacity_provider = aws_ecs_capacity_provider.api-cluster-capacity-provider.name
#    weight            = 100
#  }
  load_balancer {
   target_group_arn = aws_lb_target_group.api_target_group.arn
   container_name   = "mealplanner-api"
   container_port   = 8080
 }

#  network_configuration {
#    subnets         = data.aws_subnet_ids.default_subnet.ids
#    security_groups = ["${aws_security_group.test_security_group.id}"]
#  }

}

resource "aws_launch_template" "ecs_launch_template" {
  name_prefix = "ecs-launch-template-"
  image_id = "ami-0dd8b106eaa4021d7"
  instance_type = "t2.micro"
  key_name = aws_key_pair.ec2key.key_name
  iam_instance_profile {
    arn = aws_iam_instance_profile.ec2_instance_role_profile.arn

  }

  vpc_security_group_ids = [aws_security_group.test_security_group.id]

  block_device_mappings {
    device_name = "/dev/xvda"
    ebs {
      volume_size = 30
      volume_type = "gp2"
    }
  }


 tag_specifications {
   resource_type = "instance"
   tags = {
     Name = "ecs-instance"
   }
 }

  user_data = filebase64("${path.module}/templates/launch_template_user_data.sh")
}

resource "aws_iam_role" "ec2_instance_role" {
  name               = "EC2_InstanceRole2"
  assume_role_policy = data.aws_iam_policy_document.ec2_instance_role_policy.json
}

resource "aws_iam_role_policy_attachment" "ec2_instance_role_policy" {
  role       = aws_iam_role.ec2_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ec2_instance_role_profile" {
  name  = "EC2_InstanceRoleProfile2"
  role  = aws_iam_role.ec2_instance_role.id
}

data "aws_iam_policy_document" "ec2_instance_role_policy" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = [
        "ec2.amazonaws.com",
        "ecs.amazonaws.com"
      ]
    }
  }
}


# resource "aws_iam_role" "ecs_service_role" {
#   name               = "ECS_ServiceRole"
#   assume_role_policy = data.aws_iam_policy_document.ecs_service_policy.json
# }

# data "aws_iam_policy_document" "ecs_service_policy" {
#   statement {
#     actions = ["sts:AssumeRole"]
#     effect  = "Allow"

#     principals {
#       type        = "Service"
#       identifiers = ["ecs.amazonaws.com",]
#     }
#   }
# }

# resource "aws_iam_role_policy" "ecs_service_role_policy" {
#   name   = "ECS_ServiceRolePolicy"
#   policy = data.aws_iam_policy_document.ecs_service_role_policy.json
#   role   = aws_iam_role.ecs_service_role.id
# }

# data "aws_iam_policy_document" "ecs_service_role_policy" {
#   statement {
#     effect  = "Allow"
#     actions = [
#       "ec2:AuthorizeSecurityGroupIngress",
#       "ec2:Describe*",
#       "elasticloadbalancing:DeregisterInstancesFromLoadBalancer",
#       "elasticloadbalancing:DeregisterTargets",
#       "elasticloadbalancing:Describe*",
#       "elasticloadbalancing:RegisterInstancesWithLoadBalancer",
#       "elasticloadbalancing:RegisterTargets",
#       "ec2:DescribeTags",
#       "logs:CreateLogGroup",
#       "logs:CreateLogStream",
#       "logs:DescribeLogStreams",
#       "logs:PutSubscriptionFilter",
#       "logs:PutLogEvents"
#     ]
#     resources = ["*"]
#   }
# }

resource "aws_autoscaling_group" "api_asg" {
    name                      = "api-asg"
    vpc_zone_identifier       = data.aws_subnet_ids.default_subnet.ids
    launch_template {
        id = aws_launch_template.ecs_launch_template.id
        version = "$Latest"
    }

    desired_capacity          = 1
    min_size                  = 0
    max_size                  = 1
    health_check_grace_period = 300
    health_check_type         = "EC2"
}

data "aws_iam_policy_document" "ecs_agent" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_agent" {
  name               = "ecs-agent"
  assume_role_policy = data.aws_iam_policy_document.ecs_agent.json
}


# resource "aws_iam_role_policy_attachment" "ecs_agent" {
#   role       = aws_iam_role.ecs_agent.name
#   policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
# }

resource "aws_iam_instance_profile" "ecs_agent" {
  name = "ecs-agent"
  role = aws_iam_role.ecs_agent.name
}

resource "aws_ecs_capacity_provider" "api-cluster-capacity-provider" {
 name = "api-cluster-capacity-provider"

 auto_scaling_group_provider {
   auto_scaling_group_arn = aws_autoscaling_group.api_asg.arn

   managed_scaling {
     maximum_scaling_step_size = 1000
     minimum_scaling_step_size = 1
     status                    = "ENABLED"
     target_capacity           = 1
   }
 }
}

resource "aws_ecs_cluster_capacity_providers" "example" {
 cluster_name = aws_ecs_cluster.api-cluster.name
 capacity_providers = [aws_ecs_capacity_provider.api-cluster-capacity-provider.name]

 default_capacity_provider_strategy {
   base              = 1
   weight            = 100
   capacity_provider = aws_ecs_capacity_provider.api-cluster-capacity-provider.name
 }
}

resource "aws_security_group" "test_security_group" {
 name   = "ecs-test-security-group2"
 vpc_id = data.aws_vpc.default_vpc.id

 ingress {
   from_port   = 0
   to_port     = 0
   protocol    = -1
   self        = "false"
   cidr_blocks = ["0.0.0.0/0"]
   description = "any"
 }

# ssh
  ingress {
   from_port   = 22
   to_port     = 22
   protocol    = "tcp"
#    self        = "false"
   cidr_blocks = ["0.0.0.0/0"]
#    description = "any"
 }

 egress {
   from_port   = 0
   to_port     = 0
   protocol    = "-1"
   cidr_blocks = ["0.0.0.0/0"]
 }
}
