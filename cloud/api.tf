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

data "template_file" "ec2_instance_role_policy" {
  template = file("${path.module}/templates/ec2_instance_role.tpl")
}

resource "aws_ecs_cluster" "api-cluster" {
  name = "api"
}

resource "aws_ecs_task_definition" "api-task" {
  family = "service"
  network_mode = "bridge"
  requires_compatibilities = ["EC2"] 

  container_definitions = data.template_file.api_task_definition.rendered
}

resource "aws_ecs_service" "api-service" {
  name            = "api-service"
  cluster         = aws_ecs_cluster.api-cluster.id
  task_definition = aws_ecs_task_definition.api-task.arn
  desired_count   = 1
  launch_type     = "EC2" 

  load_balancer {
   target_group_arn = aws_lb_target_group.api_target_group.arn
   container_name   = "mealplanner-api"
   container_port   = 8080
 }
}

# IAM Roles

resource "aws_iam_role" "ec2_instance_role" {
  name               = "EC2_InstanceRole"
  assume_role_policy = data.template_file.ec2_instance_role_policy.rendered
}

resource "aws_iam_role_policy_attachment" "ec2_instance_role_policy" {
  role       = aws_iam_role.ec2_instance_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ec2_instance_role_profile" {
  name  = "EC2_InstanceRoleProfile2"
  role  = aws_iam_role.ec2_instance_role.id
}

# Capacity Providers

resource "aws_autoscaling_group" "api_asg" {
    name                      = "api-asg"
    vpc_zone_identifier       = data.aws_subnet_ids.default_subnet.ids
    launch_template {
        id = aws_launch_template.ecs_launch_template.id
        version = "$Latest"
    }

    desired_capacity          = 1
    min_size                  = 0
    max_size                  = 2
    health_check_grace_period = 300
    health_check_type         = "ELB"
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

resource "aws_ecs_cluster_capacity_providers" "api_capacity_providers" {
 cluster_name = aws_ecs_cluster.api-cluster.name
 capacity_providers = [aws_ecs_capacity_provider.api-cluster-capacity-provider.name]

 default_capacity_provider_strategy {
   base              = 1
   weight            = 100
   capacity_provider = aws_ecs_capacity_provider.api-cluster-capacity-provider.name
 }
}

#  Launch Template

resource "aws_launch_template" "ecs_launch_template" {
  name_prefix = "ecs-launch-template-"
  image_id = "ami-0dd8b106eaa4021d7"
  instance_type = "t2.micro"
  key_name = aws_key_pair.ec2key.key_name
  iam_instance_profile {
    arn = aws_iam_instance_profile.ec2_instance_role_profile.arn
  }

  vpc_security_group_ids = [aws_security_group.ecs_security_group.id]

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

  user_data = base64encode(
    <<-EOF
    #!/bin/bash
    echo ECS_CLUSTER=${aws_ecs_cluster.api-cluster.name} >> /etc/ecs/ecs.config;
    EOF
  )
}
