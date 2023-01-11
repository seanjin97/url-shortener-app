include {
  path = find_in_parent_folders()
}

terraform {
  source = "git::https://github.com/terraform-aws-modules/terraform-aws-alb.git//?ref=v8.2.1"
  extra_arguments "init_args" {
    commands = [
      "init"
    ]
    arguments = []
  }
}

dependency "vpc" {
  config_path  = "../vpc"
}

dependency "external-security-group" {
  config_path = "../external-security-group"
}

inputs =  {
  name = "url-shortener-external-alb"
  load_balancer_type = "application"
  vpc_id = dependency.vpc.outputs.vpc_id
  subnets = dependency.vpc.outputs.public_subnets
  security_groups = [dependency.external-security-group.outputs.security_group_id]
  internal = false
  target_groups = [
    {
      name = "placeholder"
      backend_protocol = "HTTP"
      backend_port = "8080"
    }
  ]
  http_tcp_listeners = [
    {
      port     = "80"
      protocol = "HTTP"
    }
  ]
  tags = {
    Terraform = "true"
    Environment = "dev"
  }
}