resource "aws_lb" "load_balancer" {
  name = "mealplanner-lb"
  load_balancer_type = "application"
  subnets = data.aws_subnet_ids.default_subnet.ids
  security_groups = [aws_security_group.alb.id]
  
}


# todo make alb for ec2 api instances
# shouldnt need http redirecting?
resource "aws_lb_listener_rule" "instances" {
  listener_arn = aws_lb_listener.https.arn
  priority = 10

  condition {
    path_pattern {
      values = ["*"]
    }
  }

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.api_target_group.arn
  }

#   action {
#     type = "redirect"

# # This currently redirects all traffic to hardcoded ui s3 bucket
#     # redirect {
#     #   port = "443"
#     #   protocol = "HTTPS"

#     #   status_code = "HTTP_302"
#     #   # host = aws_s3_bucket.ui_bucket.bucket_domain_name #This generates wrong URL
#     #   # host = aws_s3_bucket.ui_bucket.website_domain #This is deprecated, unsure what to use instead
#     #   # host = "wmacalester-mealplanner-ui-bucket.s3-website.eu-west-2.amazonaws.com"
#     # }
#   }
}

resource "aws_lb_target_group" "api_target_group" {
  name = "api-target-group"
  port = 8080
  protocol = "HTTP"
  vpc_id = data.aws_vpc.default_vpc.id
  target_type = "instance"

  # health_check {
  #   path = "/api/status"
  #   protocol = "HTTP"
  #   matcher = "200"
  #   interval = 15
  #   timeout = 3
  #   healthy_threshold = 2
  #   unhealthy_threshold = 2
  # }
}

# resource "aws_lb_target_group_attachment" "api" {
#   target_group_arn = aws_lb_target_group.api_target_group.arn
#   target_id = aws_autoscaling_group.api_asg.launch_configuration
#   port = 8080
# }


# resource "aws_lb_listener" "http" {
#   load_balancer_arn = aws_lb.load_balancer.arn

#   port = 80
#   protocol = "HTTP"

#   default_action {
#     type = "redirect"

#     redirect {
#       port = 443
#       protocol = "HTTPS"
#       status_code = "HTTP_301"
#     }
#   }
# }

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.load_balancer.arn

  port = 443
  protocol = "HTTPS"
  ssl_policy = "ELBSecurityPolicy-2016-08"
  certificate_arn = aws_acm_certificate.mealplanner_ssl_cert.arn

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "404: page not found"
      status_code = 404
    }
  }
}
