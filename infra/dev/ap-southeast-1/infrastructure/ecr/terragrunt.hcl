include {
  path = find_in_parent_folders()
}

terraform {
  source = "git::https://github.com/terraform-aws-modules/terraform-aws-ecr.git//?ref=v1.5.1"
  extra_arguments "init_args" {
    commands = [
      "init"
    ]
    arguments = []
  }
}

inputs = {
  repository_name = "private-common-repo"
  repository_read_write_access_arns = ["arn:aws:iam::784681298033:user/tf_user"]
  repository_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1,
        description  = "Keep last 5 images",
        selection = {
          tagStatus     = "tagged",
          tagPrefixList = ["v"],
          countType     = "imageCountMoreThan",
          countNumber   = 5
        },
        action = {
          type = "expire"
        }
      }
    ]
  })
  tags = {
    Terraform   = "true"
    Environment = "dev"
  }
}