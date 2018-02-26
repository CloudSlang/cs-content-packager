<a href="http://cloudslang.io/">
    <img src="https://camo.githubusercontent.com/ece898cfb3a9cc55353e7ab5d9014cc314af0234/687474703a2f2f692e696d6775722e636f6d2f696849353630562e706e67" alt="CloudSlang logo" title="CloudSlang" align="right" height="60"/>
</a>

CloudSlang Content Packager
===========================

| Module | Travis | Release | Description |
| ----- | ----- | ----- | ----- |
| cs-content-packager | [![Build Status](https://travis-ci.org/CloudSlang/cs-content-packager.svg?branch=master)](https://travis-ci.org/CloudSlang/cs-content-packager) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.cloudslang.tools/cs-content-packager/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.cloudslang.tools/cs-content-packager) | Maven - CloudSlang content package helper. |

This repository contains the sources and a maven plugin that generates the metadata and downloads dependencies for existing content from [Cloudslang/cs-content](https://github.com/CloudSlang/cs-content)

1. [General Usage](#general-usage)
2. [Contribution Guideline](#contribution-guideline)

<a name="general-usage"/>

### Pre-Requisites:
    Maven version >= 3.0.3
    Java JDK version >= 8
    M2_HOME

## General usage for - cs-content-packager-plugin

This plugin can be added as another step in a Maven build to generate content description properties file(s) 
and/or download content dependencies (for operations that use Java @Actions). 

Maven plugin example:

```
    [...]
      <plugins>
            <plugin>
                <groupId>io.cloudslang.tools</groupId>
                <artifactId>cs-content-packager-plugin</artifactId>
                <version>RELEASE</version>
                <executions>
                    <execution>
                        <id>extract metadata</id>
                        <phase>prepare-packages</phase>
                        <goals>
                            <goal>extract-description</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>copy content dependencies</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>copy-cloudslang-dependencies</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
      </plugins>
    [...]
``` 
   
<a name="contribution-guideline"/>                                       
                                       
## Contribution Guideline
                                       
Read our Contribution Guide [here](CONTRIBUTING.md).                                       
                              