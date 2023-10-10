resource "aws_s3_bucket" "ui_bucket" {
  bucket = "wmacalester-mealplanner-ui-bucket"
}

resource "aws_s3_bucket_website_configuration" "ui_website_config" {
  bucket = aws_s3_bucket.ui_bucket.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }
}

resource "aws_s3_bucket_ownership_controls" "ui_ownership_controls" {
  bucket = aws_s3_bucket.ui_bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_policy" "ui_bucket_policy" {
  bucket = aws_s3_bucket.ui_bucket.id

  policy = jsonencode({
  Version = "2012-10-17"
  Statement = [
    {
    Sid       = "PublicReadGetObject"
    Effect    = "Allow"
    Principal = "*"
    Action    = "s3:GetObject"
    Resource = [
      aws_s3_bucket.ui_bucket.arn,
      "${aws_s3_bucket.ui_bucket.arn}/*",
    ]
    },
  ]
  })
}
