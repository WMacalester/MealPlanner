import {
  to = aws_acm_certificate.mealplanner_ssl_cert
  id = "arn:aws:acm:eu-west-2:919682946353:certificate/5809daea-48d7-40d0-8a89-7c6cdaf18bc9"
}

resource "aws_acm_certificate" "mealplanner_ssl_cert" {
  options {
    certificate_transparency_logging_preference = "DISABLED"
  }
}
