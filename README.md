[![Build Status](https://travis-ci.org/anr-ru/base.services.svg?branch=master)](https://travis-ci.org/anr-ru/base.services)

## A part of [Base.Platform Project](https://github.com/anr-ru/base.platform.parent)

# Base.Services

Base prototypes for business logic implementations:

1. BaseEntity (JPA), DAO configurations and repositories to work both local and JTA environments.

2. Base classes and prototypes for business logic implementations which support text resources, a security layer
   and xml/json serialization.
   
3. APICommandFactory pattern - a annotation-based way to implement REST API command processing in the business logic level.

4. A convenient way for extensions of services by adding different plug-ins. 

5. A configuration for easy managing the DEV/QA/CI/PROD condiguration stages in the code.
 