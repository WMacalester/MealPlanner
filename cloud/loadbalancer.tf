resource "aws_lb" "load_balancer" {
  name = "mealplanner-lb"
  load_balancer_type = "application"
  subnets = data.aws_subnet_ids.default_subnet.ids
  security_groups = [aws_security_group.alb.id]
}

resource "aws_lb_listener_rule" "instances" {
  listener_arn = aws_lb_listener.https.arn
  priority = 10

  condition {
    path_pattern {
      values = ["/api/*"]
    }
  }

  action {
    type = "forward"
    target_group_arn = aws_lb_target_group.api_target_group.arn
  }
}

resource "aws_lb_target_group" "api_target_group" {
  name = "api-target-group"
  port = 8080
  protocol = "HTTP"
  vpc_id = data.aws_vpc.default_vpc.id
  target_type = "instance"

  health_check {
    path = "/api/status"
    protocol = "HTTP"
    port = 8080
    matcher = "200"
    interval = 15
    timeout = 3
    healthy_threshold = 2
    unhealthy_threshold = 2
  }
}

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
      message_body = "Forbidden"
      status_code = 403
    }
  }
}
