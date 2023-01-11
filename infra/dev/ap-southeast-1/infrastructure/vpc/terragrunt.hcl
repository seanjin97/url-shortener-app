include {
  path = find_in_parent_folders()
}

terraform {
  source = "git::https://github.com/terraform-aws-modules/terraform-aws-vpc.git//?ref=v3.18.1"
  extra_arguments "init_args" {
    commands = [
      "init"
    ]
    arguments = []
  }
}

inputs = {
  name = "url-shortener-vpc"
  cidr = "10.0.0.0/16"
  azs = ["ap-southeast-1a", "ap-southeast-1b"]
  public_subnets = ["10.0.101.0/24", "10.0.102.0/24"]
  intra_subnets = ["10.0.1.0/24", "10.0.2.0/24"]
  enable_nat_gateway = false
  enable_vpn_gateway = false
  tags = {
    Terraform = "true"
    Environment = "dev"
  }
}