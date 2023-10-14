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

  origin {
    domain_name = aws_lb.load_balancer.dns_name
    origin_id = aws_lb.load_balancer.id

    custom_origin_config {
      http_port = 80
      https_port = 443
      origin_protocol_policy = "https-only" 
      origin_ssl_protocols = ["TLSv1.2"]
    }
  }

  enabled = true
  is_ipv6_enabled = true
  default_root_object = "index.html"

  aliases = [var.domain_alias]

  # Cache behavior for s3/ui
  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = aws_s3_bucket.ui_bucket.arn

    cache_policy_id = aws_cloudfront_cache_policy.caching_optimized_policy.id

    viewer_protocol_policy = "redirect-to-https"
  }

  # Cache behavior with precedence 0 for loadbalancer/api
  ordered_cache_behavior {
    path_pattern     = "/api/*"
    allowed_methods  = ["GET", "POST", "HEAD", "PUT", "PATCH", "DELETE", "OPTIONS"]
    cached_methods   = ["GET", "HEAD", "OPTIONS"]
    target_origin_id = aws_lb.load_balancer.arn
    
    compress               = true
    viewer_protocol_policy = "redirect-to-https"
    cache_policy_id = aws_cloudfront_cache_policy.caching_disabled_policy.id
    origin_request_policy_id = aws_cloudfront_origin_request_policy.all_viewer_policy.id
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

  # logging_config {
  #   bucket = aws_s3_bucket.cloudfront_logs.bucket_domain_name
  #   include_cookies = false
  #   prefix = "cloudfront-logs/"
  # }

  # For correct behaviour with react-router
  custom_error_response {
    error_code = 403
    response_code = 200
    response_page_path = "/index.html"
  }
}

resource "aws_cloudfront_origin_request_policy" "all_viewer_policy" {
  name    = "allViewer-policy"
  comment = "allViewer comment"
  cookies_config {
    cookie_behavior = "all"
  }
  headers_config {
    header_behavior = "allViewer"
  }
  query_strings_config {
    query_string_behavior = "all"
  }
}

resource "aws_cloudfront_cache_policy" "caching_optimized_policy" {
  name        = "caching-optimized-policy"
  default_ttl = 86400
  max_ttl     = 31536000
  min_ttl     = 1
  parameters_in_cache_key_and_forwarded_to_origin {
    cookies_config {
      cookie_behavior = "none"
    }
    headers_config {
      header_behavior = "none"
    }
    query_strings_config {
      query_string_behavior = "none"
    }
  }
}

resource "aws_cloudfront_cache_policy" "caching_disabled_policy" {
  name        = "caching-disabled-policy"
  default_ttl = 0
  max_ttl     = 0
  min_ttl     = 0
  parameters_in_cache_key_and_forwarded_to_origin {
    cookies_config {
      cookie_behavior = "none"
    }
    headers_config {
      header_behavior = "none"
    }
    query_strings_config {
      query_string_behavior = "none"
    }
  }
}

# ------
# For logging:

# resource "aws_s3_bucket" "cloudfront_logs" {
#   bucket = "wmacalester-mealplanner-cloudfront-logs"
# }

# resource "aws_s3_bucket_ownership_controls" "cloudfront_logs_ownership_controls" {
#   bucket = aws_s3_bucket.cloudfront_logs.id
#   rule {
#     object_ownership = "BucketOwnerPreferred"
#   }
# }

# resource "aws_s3_bucket_acl" "ui_bucket_acl" {
#   depends_on = [aws_s3_bucket_ownership_controls.cloudfront_logs_ownership_controls]

#   bucket = aws_s3_bucket.cloudfront_logs.id
#   acl    = "log-delivery-write"
# }
