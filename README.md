<!--
 |    Copyright © 2013 Konstantin Livitski
 | 
 |    This file is part of StAXform. StAXform is
 |    licensed under the Apache License, Version 2.0 (the "License");
 |    you may not use this file except in compliance with the License.
 |    You may obtain a copy of the License at
 | 
 |      http://www.apache.org/licenses/LICENSE-2.0
 | 
 |    Unless required by applicable law or agreed to in writing, software
 |    distributed under the License is distributed on an "AS IS" BASIS,
 |    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 |    See the License for the specific language governing permissions and
 |    limitations under the License.
 -->

<a name="sec-about"> </a>
About StAXform
==============
StAXform is a library that makes it easy to create transformers out of
[XML event pipelines][xpipe] in Java. With StAXform, you can write
software that transforms streams of XML events compatible with [StAX][] API and
wrap it in a [transformer object][TrAX] (also known as [TrAX][] instance). The
users of your software receive a drop-in replacement of an XSLT transformer,
and you don't have to write a single line of [XSLT][].

<a name="sec-download"> </a>
Downloading the binary
======================

The compiled binary of StAXform is available for download at:

 - <https://github.com/StanLivitski/StAXForm/wiki/Download>

<a name="sec-use"> </a>
Using StAXform
==============

Client applications usually obtain an instance of `XMLEventTransformer` class
and use it is a `javax.xml.transform.Transformer`. The easiest way to obtain a
transformer object is by calling the no-arguments constructor:

     import javax.xml.transform.Transformer;
     import name.livitski.tools.xml.staxform.XMLEventTransformer;

...

      Transformer xformer = new XMLEventTransformer();

The result is an identity transformer that copies the source XML to the output
target. In most cases though, you want to intervene in the process by having
your code either examine the XML input or modify the XML result. To do that,
first create an object that implements the `XMLEventProcessor` interface.

The `XMLEventProcessor` object constitutes your XML transformation pipeline.
It combines methods from `javax.xml.stream.util.XMLEventConsumer` and a
`java.util.Iterator` over `XMLEvent` objects. Thus, it can receive a stream of
source XML events pushed by the transformer and generate resulting events to be
pulled out. The detailed explanation of `XMLEventProcessor` implementation
requirements is given in the [API documentation](#sec-api).

Note that the processor objects that generate XML events must be able to store 
them until polled by the caller. An easy way to do that is by subclassing
`IdentityEventProcessor`, the StAXform's implementation of `XMLEventProcessor`
that performs identity transformation of XML events, and overriding its
`add(XMLEvent)` method.

<!-- TODO: IdentityEventProcessor subclassing example -->

StAXform provides several other built-in implementations of
`XMLEventProcessor`:

 - `NullEventProcessor` discards any input it receives and produces no
output  
 - `EventProcessorChain` connects two `XMLEventProcessor` instances with
a pipeline, so that events resulting from the first processor are pushed
to the second one
 - `ConditionalEventProcessor` applies the target `XMLEventProcessor`
to events that pass a filter and performs identity transformation of the
events that don't

Once you have an event processor object that implements the required
transformation, you may wrap it in a `Transformer` by passing that object
to the `XMLEventTransformer`'s constructor:

     import javax.xml.transform.Transformer;
     import name.livitski.tools.xml.staxform.XMLEventTransformer;
     import name.livitski.tools.xml.staxform.XMLEventProcessor;

...

      XMLEventProcessor pipeline;
      // initialize your pipeline here

...

      Transformer xformer = new XMLEventTransformer(pipeline);

You can also assign an event processor to an existing transformer:

      XMLEventTransformer xformer = new XMLEventTransformer();
      xformer.setFunction(pipeline);

Once you have an `XMLEventTransformer` object set up, use it as a
regular `javax.xml.transform.Transformer`:

      xformer.transform(source, result);

Note that the alpha version of the StAXform library implements only the basic
functionality of a TrAX transformer. If you would like to have a more complete
TrAX implementation, please consider contributing the missing features, or
[contact the project's team](#sec-contact) to request enhancements. 
 
<a name="sec-api"> </a>
StAXform API
------------

To browse the Javadoc for StAXform, please navigate to: 

 - <http://stanlivitski.github.io/StAXForm/javadoc/index.html>

<a name="sec-repo"> </a>
About this repository
=====================

This repository contains StAXform's sources, license information, and files
required to build the project and work with its source code. The top-level
entries of this repository are: 

        src/           		StAXform's source files
        LICENSE		        Document that describes the project's licensing
        					 terms
        NOTICE   	        A summary of license terms that apply to StAXform 
        build.xml      		Configuration file for the tool (Ant) that builds
                       		 the StAXform binary
        .classpath     		Eclipse configuration file for the project
        .project       		Eclipse configuration file for the project
        README.md			This document

<a name="sec-building"> </a>
Building StAXform
=================

To build the StAXform's binary from this repository, you need:

   - A **Java SDK**, also known as JDK, Standard Edition (SE), version 6 or
   later, available from OpenJDK <http://openjdk.java.net/> or Oracle
   <http://www.oracle.com/technetwork/java/javase/downloads/index.html>.

   Even though a Java runtime may already be installed on your machine
   (check that by running `java --version`), the build will fail if you
   don't have a complete JDK (check that by running `javac`).

   - **Apache Ant** version 1.7.1 or newer, available from the Apache Software
   Foundation <http://ant.apache.org/>.

To build StAXform, go to the directory containing its working copy and run:

     ant

The result is a file named `staxform.jar` in the same directory. 

<a name="sec-contact"> </a>
Contacting the project's team
=============================

You can send a message to the project's team via the
[Contact page](http://www.livitski.com/contact) at <http://www.livitski.com/>
or via *GitHub*. We will be glad to hear from you!

   [xpipe]: https://en.wikipedia.org/wiki/XML_Pipelines
   [StAX]: http://jcp.org/en/jsr/detail?id=173
   [TrAX]: http://xml.apache.org/xalan-j/trax.html
   [XSLT]: https://en.wikipedia.org/wiki/XSL_Transformations
