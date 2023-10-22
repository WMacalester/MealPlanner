resource "aws_key_pair" "ec2key" {
  key_name = "ec2key"
  public_key = file("~/.ssh/ec2key.pub")
}