## scala monorepo

dev monorepo that I try to maintain personal scala code for useful libraries / general patterns

scala 2.13.x

might be useful if you use

[Kafka](https://kafka.apache.org/quickstart)

[Redpanda](https://docs.redpanda.com/current/home/)

[Spark](https://spark.apache.org/)

[Hadoop](https://hadoop.apache.org/)

[OpenSearch](https://opensearch.org/)

For Scripting a basic http client is included too in **scala/`http`** and Lang utils are included in **scala/`lang`**

I mostly build from source and run on bare metal _Ubuntu_ / _Amazon Linux 2_ / _KVM_ on _ARM_ and _x86_ - so most of the tools and code is built and unit tested against that environment.

## Building

[kvmetal](https://github.com/kuro337/kvmetal) : If you wish to launch any of it on Metal or Kubernetes - I maintain tooling to leverage KVM for launching these components and handling Networking - or launching on Kubernetes.
