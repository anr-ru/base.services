[![Build Status](https://travis-ci.org/anr-ru/base.services.svg?branch=master)](https://travis-ci.org/anr-ru/base.services)

## A part of [Base.Platform Project](https://github.com/anr-ru/base.platform.parent)

# Base.Services

**Base.Service** was created as a framework for fast creating business logic
operations.

It contains the following ready prototypes:

1. The base domain object - [BaseEntity](./src/main/java/ru/anr/base/domain/BaseEntity.java),
   data access configurations and repositories to work both in local and JTA environments.

2. Base classes and prototypes for business logic implementations ([BaseServiceImpl](./src/main/java/ru/anr/base/services/BaseServiceImpl.java),
   [BaseDataAwareServiceImpl](./src/main/java/ru/anr/base/services/BaseDataAwareServiceImpl.java))
   which support text resources, a security layer and xml/json serialization and lots
   of other convenient functions for validation, verifications and transformations.

3. APICommand/[ApiStrategy](./src/main/java/ru/anr/base/services/api/ApiCommandStrategy.java) framework/pattern -
   an annotation-based way to implement REST API commands on the business logic level, e.g. in the EJB environment.

4. A convenient way for extensions of services by adding different plug-ins.

5. A configuration for easy managing the DEV/QA/CI/PROD configuration stages in the code.

6. Authentication/Authorization prototypes with ACL and role-based authorization models.
