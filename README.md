## References

The project is based on the tutorials/quickstarts
 * [Quarkus - Amazon Lambda with RESTEasy, Undertow, or Vert.x Web](https://quarkus.io/guides/amazon-lambda-http)
 * [How to use the Vert.x extension](https://github.com/quarkusio/quarkus-quickstarts/tree/master/vertx-quickstart)
 
Modifications done within his project

 * Using Gradle instead of Maven
 * DB migrations instead of programmatic DB setup
 * Added Testcontainers to set up Postgres in unit tests
 * Added several SAM command lines as Gradle tasks

## Configuration

 * Required installations
   * AWS CLI
   * AWS SAM CLI
   * Docker
 * Required environment variables configured in AWS for the Lambda
   * QUARKUS_DATASOURCE_USERNAME
   * QUARKUS_DATASOURCE_PASSWORD
   * QUARKUS_DATASOURCE_JDBC_URL
   * QUARKUS_DATASOURCE_REACTIVE_URL
 * Existing `.env.json` configuration file in the project root directory, see `.env.json.template`

## Lessons learned

### Performance

 * The native image uses less memory than the Java 11 runtime, the costs of a 64M memory block could be saved at AWS.
 * The native image has a significantly lower cold start delay, several seconds less than the Java 11 runtime. 
 A cold start delay of one second or less should be achievable.

### Current problems

 * The `quarkusDev` Gradle task and `@NativeImageTest` are not working since the container tries to reach a nonexistent 
 AWS Lambda poll URL at startup.
 * The `startLocally` ans `startNativeLocally` custom Gradle tasks cannot be stopped cleanly by CTRL+C or by IDEA.

### Access both RDS and public internet resources from the same Lambda

 - The **correct** solution
   - Create a new security group for the Lambda and configure access from the RDS security group, as described 
   [here](https://blog.shikisoft.com/running-aws-lambda-in-vpc-accessing-rds/).
   - Configure at least one new subnet and enable internet access over a NAT gateway (**extra costs**), 
   as described [here](https://aws.amazon.com/de/premiumsupport/knowledge-center/internet-access-lambda-function/).
   - Configure security group and subnet(s) in a [VpcConfig](https://docs.aws.amazon.com/lambda/latest/dg/API_VpcConfig.html) in `sam.*.yml`. 
   Make sure the Lamba has the `AWSLambdaVPCAccessExecutionRole` permission.
 - The **development** solution (since the correct solution requires a NAT gateway which is charged hourly, this 
 contradicts the idea of just testing a Lambda with minimal costs or even within the AWS free tier)
   - Configure the RDS instance as publicly accessible (and implicitly accept the related **security risks**). By default a Lambda 
   runs in a secure VPC with access to AWS services and the internet (but not internal resources like RDS).
