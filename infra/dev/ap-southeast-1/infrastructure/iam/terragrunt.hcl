include {
  path = find_in_parent_folders()
}

terraform {
  source = "git::https://github.com/terraform-aws-modules/terraform-aws-iam.git//?ref=v5.10.0"
  extra_arguments "init_args" {
    commands = [
      "init"
    ]
    arguments = []
  }
}
