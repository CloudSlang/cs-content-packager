<a href="http://cloudslang.io/">
    <img src="https://camo.githubusercontent.com/ece898cfb3a9cc55353e7ab5d9014cc314af0234/687474703a2f2f692e696d6775722e636f6d2f696849353630562e706e67" alt="CloudSlang logo" title="CloudSlang" align="right" height="60"/>
</a>

CloudSlang Content Packager
============================

[![Build Status](https://travis-ci.org/CloudSlang/cs-content-packager.svg?branch=master)](https://travis-ci.org/CloudSlang/cs-content-packager)


This repository contains a tool that packages existing content from [Cloudslang/cloud-slang-content](https://github.com/CloudSlang/cloud-slang-content).

2. [General Usage](#general-usage)
3. [Contribution Guideline](#contribution-guideline)

<a name="general-usage"/>

## General usage

cs-content-packager-plugin 

This plugin can also be added as another step in a Maven build to generate content description properties files and 
download cs-content dependencies.

Maven plugin example:

```
    [...]
      <plugins>
            <plugin>
                <groupId>io.cloudslang.tools</groupId>
                <artifactId>cs-content-packager-plugin</artifactId>
                <version>${version}</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>generate</goal>
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
                              