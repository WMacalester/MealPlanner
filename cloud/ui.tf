# todo set bucket to private!

resource "aws_s3_bucket" "ui_bucket" {
  bucket = "mealplanner-ui-bucket"
}

data "template_file" "ui_routing_rules" {
  template = file("${path.module}/templates/ui_error_routing_rules.tpl")
  vars = {
    hostname = var.domain_alias
  }
}

resource "aws_s3_bucket_website_configuration" "ui_website_config" {
  bucket = aws_s3_bucket.ui_bucket.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }

  routing_rules = data.template_file.ui_routing_rules.rendered
}

resource "aws_s3_bucket_ownership_controls" "ui_ownership_controls" {
  bucket = aws_s3_bucket.ui_bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

data "template_file" "ui_bucket_policy" {
  template = file("${path.module}/templates/ui_bucket_policy.tpl")
  vars = {
    bucket_arn = aws_s3_bucket.ui_bucket.arn
  }
}

resource "aws_s3_bucket_policy" "ui_bucket_policy" {
  bucket = aws_s3_bucket.ui_bucket.id

  policy = data.template_file.ui_bucket_policy.rendered
}
