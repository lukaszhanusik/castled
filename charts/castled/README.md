## Parameters

### Global Parameters

| Name                   | Description                                  | Value |
| ---------------------- | -------------------------------------------- | ----- |
| `global.imageRegistry` | Global Docker image registry                 | `""`  |
| `global.storageClass`  | Global StorageClass for Persistent Volume(s) | `""`  |


### Common Parameters

| Name                         | Description                                                                                                         | Value           |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------------- | --------------- |
| `nameOverride`               | String to partially override castled.fullname template with a string (will prepend the release name)                | `""`            |
| `fullnameOverride`           | String to fully override castled.fullname template with a string                                                    | `castled`       |
| `serviceAccount.annotations` | Annotations for service account. Evaluated as a template. Only used if `create` is `true`.                          | `{}`            |
| `serviceAccount.create`      | Specifies whether a ServiceAccount should be created                                                                | `true`          |
| `serviceAccount.name`        | Name of the service account to use. If not set and create is true, a name is generated using the fullname template. | `castled-admin` |


### Castled webapp Parameters

| Name                         | Description                                                      | Value                   |
| ---------------------------- | ---------------------------------------------------------------- | ----------------------- |
| `webapp.replicaCount`        | Number of webapp replicas                                        | `1`                     |
| `webapp.image.repository`    | The repository to use for the castled webapp image.              | `castled/webapp`        |
| `webapp.image.pullPolicy`    | the pull policy to use for the Castled webapp image              | `IfNotPresent`          |
| `webapp.image.tag`           | The Castled webapp image tag. Defaults to the chart's AppVersion | `0.2.21-alpha`          |
| `webapp.podAnnotations`      | Add extra annotations to the webapp pod(s)                       | `{}`                    |
| `webapp.securityContext`     | Security context for the container                               | `{}`                    |
| `webapp.service.type`        | The service type to use for the webapp service                   | `ClusterIP`             |
| `webapp.service.port`        | The service port to expose the webapp on                         | `80`                    |
| `webapp.ingress.enabled`     | Set to true to enable ingress record generation                  | `false`                 |
| `webapp.ingress.className`   | Specifies ingressClassName for clusters >= 1.18+                 | `""`                    |
| `webapp.ingress.annotations` | Ingress annotations done as key:value pairs                      | `{}`                    |
| `webapp.ingress.hosts`       | The list of hostnames to be covered with this ingress record.    | `[]`                    |
| `webapp.ingress.tls`         | Custom ingress TLS configuration                                 | `[]`                    |
| `webapp.resources.limits`    | The resources limits for the scheduler container                 | `{}`                    |
| `webapp.resources.requests`  | The requested resources for the scheduler container              | `{}`                    |
| `webapp.nodeSelector`        | Node labels for pod assignment                                   | `{}`                    |
| `webapp.tolerations`         | Tolerations for scheduler pod assignment.                        | `[]`                    |
| `webapp.affinity`            | Affinity and anti-affinity for scheduler pod assignment.         | `{}`                    |
| `webapp.appBaseUrl`          | The webapp base url.                                             | `http://localhost:3000` |
| `webapp.logLevel`            | Logging level of webapp instance.                                | `INFO`                  |
| `webapp.isOss`               | Set to true if this is open source instance.                     | `true`                  |


### Castled app Parameters

| Name                      | Description                                                      | Value          |
| ------------------------- | ---------------------------------------------------------------- | -------------- |
| `app.replicaCount`        | Number of app instances.                                         | `1`            |
| `app.image.repository`    | The repository to use for the castled webapp image.              | `castled/app`  |
| `app.image.pullPolicy`    | the pull policy to use for the Castled webapp image              | `IfNotPresent` |
| `app.image.tag`           | The Castled webapp image tag. Defaults to the chart's AppVersion | `0.2.21-alpha` |
| `app.podAnnotations`      | Add extra annotations to the webapp pod(s)                       | `{}`           |
| `app.podSecurityContext`  | Pods' Security Context                                           | `{}`           |
| `app.securityContext`     | Security context for the container                               | `nil`          |
| `app.service.type`        | The service type to use for the webapp service                   | `ClusterIP`    |
| `app.service.port`        | The service port to expose the webapp on                         | `7050`         |
| `app.ingress.enabled`     | Set to true to enable ingress record generation                  | `false`        |
| `app.ingress.className`   | Specifies ingressClassName for clusters >= 1.18+                 | `""`           |
| `app.ingress.annotations` | Ingress annotations done as key:value pairs                      | `{}`           |
| `app.ingress.hosts`       | The list of hostnames to be covered with this ingress record.    | `[]`           |
| `app.ingress.tls`         | Custom ingress TLS configuration                                 | `[]`           |
| `app.resources.limits`    | The resources limits for the scheduler container                 | `{}`           |
| `app.resources.requests`  | The requested resources for the scheduler container              | `{}`           |
| `app.logLevel`            | Logging level for backend app.                                   | `INFO`         |
| `app.nodeSelector`        | Node labels for pod assignment                                   | `{}`           |
| `app.tolerations`         | Tolerations for app pod assignment.                              | `[]`           |
| `app.affinity`            | Affinity and anti-affinity for app pod assignment.               | `{}`           |


### Mysql Parameters

| Name                                         | Description                                                                                                                                                                          | Value           |
| -------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------- |
| `mysql.enabled`                              | Whether to deploy a Redis server to satisfy the applications requirements. To use an external database instance set this to `false` and configure the `externalDatabase` parameters. | `true`          |
| `mysql.nameOverride`                         | String to partially override common.names.fullname template (will maintain the release name)                                                                                         | `castled-mysql` |
| `mysql.fullnameOverride`                     | String to fully override common.names.fullname template                                                                                                                              | `""`            |
| `mysql.architecture`                         | MySQL architecture (standalone or replication)                                                                                                                                       | `standalone`    |
| `mysql.auth.database`                        | Name for a custom database to create.                                                                                                                                                | `app`           |
| `mysql.auth.username`                        | Name for a custom user to create.                                                                                                                                                    | `user`          |
| `mysql.auth.password`                        | Password for the root user. Ignored if existing secret is provided.                                                                                                                  | `user`          |
| `mysql.auth.existingSecret`                  | Use existing secret for password details.                                                                                                                                            | `""`            |
| `mysql.primary.persistence.enabled`          | Enable data persistence using PVC.                                                                                                                                                   | `false`         |
| `mysql.primary.persistence.size`             | Persistent Volume size.                                                                                                                                                              | `2Gi`           |
| `externalDatabase.host`                      | Mysql host.                                                                                                                                                                          | `""`            |
| `externalDatabase.port`                      | Mysql port.                                                                                                                                                                          | `3306`          |
| `externalDatabase.database`                  | Name for a custom database to connect to.                                                                                                                                            | `app`           |
| `externalDatabase.password`                  | Mysql password.                                                                                                                                                                      | `""`            |
| `externalDatabase.existingSecret`            | Name of an existing secret resource containing the Redis password.                                                                                                                   | `""`            |
| `externalDatabase.existingSecretPasswordKey` | Name of an existing secret key for external database password.                                                                                                                       | `""`            |


### Kafka Parameters

| Name                                  | Description                                                                                                                               | Value            |
| ------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | ---------------- |
| `kafka.enabled`                       | Whether to Install kafka on kubernetes. To use a managed kafka instance set this to `false` and configure the `externalKafka` parameters. | `true`           |
| `kafka.nameOverride`                  | Name override for kafka app                                                                                                               | `castled-kafka`  |
| `kafka.fullnameOverride`              | Fullname override for kafka app                                                                                                           | `""`             |
| `kafka.persistence.enabled`           | Enable persistence using PVC                                                                                                              | `true`           |
| `kafka.persistence.size`              | PVC Storage Request for kafka volume                                                                                                      | `5Gi`            |
| `kafka.logRetentionBytes`             | A size-based retention policy for logs. Should be less than kafka.persistence.size, minimum 1GB                                           | `_4_000_000_000` |
| `kafka.logRetentionHours`             | The minimum age of a log file to be eligible for deletion due to age                                                                      | `24`             |
| `kafka.zookeeper.enabled`             | Install zookeeper on kubernetes                                                                                                           | `true`           |
| `kafka.zookeeper.persistence.enabled` | Enable persistence using PVC                                                                                                              | `true`           |
| `kafka.zookeeper.persistence.size`    | Persistent Volume size                                                                                                                    | `5Gi`            |
| `kafka.externalZookeeper.servers`     | URL for zookeeper. Only set when internal zookeeper is disabled                                                                           | `nil`            |
| `externalKafka.url`                   | URL for external kafka. Only set when internal `kafka.enabled` is false.                                                                  | `nil`            |
| `externalKafka.host`                  | Host for kafka. Only set when internal `kafka.enabled` is false.                                                                          | `nil`            |
| `externalKafka.port`                  | Port for kafka. Only set when internal `kafka.enabled` is false.                                                                          | `nil`            |


### Redis Parameters

| Name                                      | Description                                                                                                                                                                    | Value           |
| ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------- |
| `redis.enabled`                           | Whether to deploy a Redis server to satisfy the applications requirements. To use an external redis instance set this to `false` and configure the `externalRedis` parameters. | `true`          |
| `redis.nameOverride`                      | String to partially override common.names.fullname template (will maintain the release name)                                                                                   | `castled-redis` |
| `redis.fullnameOverride`                  | String to fully override common.names.fullname template                                                                                                                        | `""`            |
| `redis.architecture`                      | Redis architecture (standalone or replication)                                                                                                                                 | `standalone`    |
| `redis.auth.enabled`                      | Enable Redis password authentication.                                                                                                                                          | `false`         |
| `redis.auth.password`                     | Redis password authentication.                                                                                                                                                 | `""`            |
| `redis.auth.existingSecret`               | The name of an existing secret containing the Redis credential to use.                                                                                                         | `""`            |
| `redis.auth.existingSecretPasswordKey`    | Password key to be retrieved from existing secret.                                                                                                                             | `""`            |
| `redis.master.persistence.enabled`        | Enable data persistence using PVC.                                                                                                                                             | `false`         |
| `redis.master.persistence.size`           | Persistent Volume size.                                                                                                                                                        | `nil`           |
| `externalRedis.host`                      | Redis host.                                                                                                                                                                    | `""`            |
| `externalRedis.port`                      | Redis port.                                                                                                                                                                    | `6379`          |
| `externalRedis.password`                  | Redis password.                                                                                                                                                                | `""`            |
| `externalRedis.existingSecret`            | RName of an existing secret resource containing the Redis password.                                                                                                            | `""`            |
| `externalRedis.existingSecretPasswordKey` | Name of an existing secret key for Redis password.                                                                                                                             | `""`            |


