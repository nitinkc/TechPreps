# Terraform (Quick Guide)

Summary
- What: Infrastructure-as-Code tool for provisioning cloud resources declaratively.
- When to use: repeatable infra provisioning, multi-environment deployments, drift detection.

Key concepts
- Providers, resources, modules, state, variables, outputs.
- Remote state backends: S3/GCS + DynamoDB/Cloud Storage for locking (or Terraform Cloud).
- Modules for reuse and organization.

Best practices
- Keep small modules with clear inputs/outputs, use remote state locking, store secrets in vaults or provider-specific secret managers.
- Review plans in CI before applying; use `terraform fmt` and `terraform validate` in pipelines.

Common interview points
- How state locking works, pros/cons of Terraform Cloud vs remote backends, module versioning and module registry usage.

## Example: Terraform (Java equivalent - create S3 bucket for remote state using AWS SDK)

```java
// Maven dependency: software.amazon.awssdk:s3
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.nio.file.Paths;

Region region = Region.US_EAST_1;
try (S3Client s3 = S3Client.builder().region(region).build()) {
  String bucket = "my-terraform-state";
  s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());

  // write a simple placeholder state file (in practice Terraform writes state)
  s3.putObject(PutObjectRequest.builder().bucket(bucket).key("state/dev/terraform.tfstate").build(),
               Paths.get("./example-state.json"));
}
```

## Workflow (CI snippet - Java runner)

```java
// Example: run terraform commands from Java CI runner (ProcessBuilder)
ProcessBuilder pb = new ProcessBuilder("terraform", "init", "-backend-config=... ");
pb.inheritIO();
Process p = pb.start();
int rc = p.waitFor();
if (rc != 0) throw new RuntimeException("terraform init failed");
// then plan/apply similarly
```
