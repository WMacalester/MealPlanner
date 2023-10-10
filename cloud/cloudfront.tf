# ssl cert is required in us-east-1 for cloudfront to work with https
variable "cloudfront_certificate_arn" {
  type = string
  default = "arn:aws:acm:us-east-1:919682946353:certificate/bfd2f49f-3c7b-4055-9e8e-62e9c6312c2f"
}

resource "aws_cloudfront_distribution" "s3_distribution" {
  origin {
    domain_name = aws_s3_bucket.ui_bucket.bucket_regional_domain_name
    origin_id = aws_s3_bucket.ui_bucket.arn
  }

  enabled = true
  is_ipv6_enabled = true
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = aws_s3_bucket.ui_bucket.arn

    forwarded_values {
      headers = []
      query_string = false

      cookies {
        forward = "all"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
  }

  price_class = "PriceClass_100"

  restrictions {
    geo_restriction {
      restriction_type = "whitelist"
      locations        = ["GB"]
    }
  }

  viewer_certificate {
    acm_certificate_arn = var.cloudfront_certificate_arn
    ssl_support_method = "sni-only"
  }
}
