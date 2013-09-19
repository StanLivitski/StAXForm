/**
 *    Copyright © 2013 Konstantin Livitski
 * 
 *    This file is part of StAXform. StAXform is
 *    licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package name.livitski.tools.xml.staxform;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import name.livitski.tools.xml.staxform.helpers.EventProcessorChain;
import name.livitski.tools.xml.staxform.helpers.IdentityEventProcessor;

/**
 * Implements basic functions of a {@link Transformer} by
 * delegating to a user-defined {@link XMLEventProcessor}.
 * If no event processor is supplied, performs the "identity
 * transformation" making a copy of the {@link Source} to
 * the {@link Result}.
 */
public class XMLEventTransformer extends AbstractTransformer
{
 /**
  * Performs identity transformation.
  * TODO: take into account the output parameters when transforming
  * NOTE: its the caller's responsibility to set up encoding of the
  * {@link Result} argument if it writes to a byte stream. The encoding
  * should match the {@link OutputKeys#ENCODING}
  * {@link #getOutputProperty(String) output property}.
  * NOTE: its the caller's responsibility to close the underlying
  * streams.
  */
 @Override
 public void transform(Source xmlSource, Result outputTarget)
   throws TransformerException
 {
  if (!"xml".equals(getOutputProperty(OutputKeys.METHOD)))
    throw new TransformerConfigurationException(
      "Transformer " + XMLEventTransformer.class
      + " does not support output method \"" + getOutputProperty(OutputKeys.METHOD) + '"'
    );
  XMLEventReader in = null;
  XMLEventWriter out = null;
  try
  {
   in = getXMLInputFactory().createXMLEventReader(xmlSource);
   out = getXMLOutputFactory().createXMLEventWriter(outputTarget);
   // Reset the transformer and receive any initial output it may have
   transformer.reset();
   while (transformer.hasNext())
   {
    XMLEvent event = transformer.next();
    out.add(event);
   }
   while (in.hasNext())
   {
    XMLEvent event = in.nextEvent();
    transformer.add(event);
    while (transformer.hasNext())
    {
     event = transformer.next();
     out.add(event);
    }
   }
  }
  catch (XMLStreamException e)
  {
   handleFatalError(transformerExceptionFrom(e));
  }
  finally
  {
   try { if (null != in) in.close(); }
   catch (XMLStreamException closeFail)
   {
    handleStreamCloseFailure(closeFail);
   }
   finally
   {
    try { if (null != out) out.close(); }
    catch (XMLStreamException closeFail)
    {
     handleStreamCloseFailure(closeFail);
    }
   }
  }
 }

 @Override
 public void reset()
 {
  lastThrown = null;
  // TODO: reset any instance vars used in transformation here 
 }

 /**
  * Creates an {@link XMLEventTransformer} that delegates to
  * an {@link IdentityEventProcessor} to perform "identity
  * transformation" of the XML stream.
  */
 public XMLEventTransformer()
 {
  setFunction(null);
 }

 /**
  * Creates an {@link XMLEventTransformer} using a custom
  * {@link XMLEventProcessor event transformation function}.
  * @param function the transformation function or
  * <code>null</code> to use an {@link IdentityEventProcessor}
  * for transformation
  */
 public XMLEventTransformer(XMLEventProcessor function)
 {
  setFunction(function);
 }

 /**
  * Changes the {@link XMLEventProcessor event transformation function}
  * used by this {@link XMLEventTransformer}.
  * @param function the transformation function or
  * <code>null</code> to use an {@link IdentityEventProcessor}
  * for transformation
  */
 public void setFunction(XMLEventProcessor function)
 {
  transformer = preprocessor();
  if (null == function)
   function = new IdentityEventProcessor();
  if (null == transformer)
   transformer = function;
  else
   transformer = new EventProcessorChain(transformer, function);
  function = postprocessor();
  if (null != function)
   transformer = new EventProcessorChain(transformer, function);
 }

 /**
  * Override to implement pre-processing of {@link XMLEvent enevts}
  * before they are fed to the
  * {@link #XMLEventTransformer(XMLEventProcessor) user-defined processor}.
  * Default implementation returns an {@link XMLHeaderProcessor}.
  * @return pre-processor for events or <code>null</code> to skip event
  * pre-processing 
  */
 protected XMLEventProcessor preprocessor()
 {
  return new XMLHeaderProcessor();
 }

 /**
  * Override to implement post-processing of {@link XMLEvent enevts}
  * received from the
  * {@link #XMLEventTransformer(XMLEventProcessor) user-defined processor}.
  * Default implementation returns <code>null</code>.
  * @return post-processor for events or <code>null</code> to skip the
  * post-processing 
  */
 protected XMLEventProcessor postprocessor()
 {
  return null;
 }

 protected XMLOutputFactory getXMLOutputFactory()
 {
  return XMLOutputFactory.newFactory();
 }

 protected XMLInputFactory getXMLInputFactory()
 {
  XMLInputFactory factory = XMLInputFactory.newFactory();
  factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
  factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
  return factory;
 }

 protected XMLEventFactory getXMLEventFactory()
 {
  if (null == eventFactory)
   eventFactory = XMLEventFactory.newFactory();
  return eventFactory;
 }

 /**
  * Changes the header of resulting XML document according to the
  * {@link #getOutputProperties() output properties}.
  */
 protected StartDocument shapeXMLHeader(StartDocument headerEvent)
 {
  String version = getOutputProperty(OutputKeys.VERSION);
  if (null == version)
   version = headerEvent.getVersion();
  String encoding = getOutputProperty(OutputKeys.ENCODING);
  if (null == encoding)
   encoding = headerEvent.getCharacterEncodingScheme();
  String standalone = getOutputProperty(OutputKeys.STANDALONE);
  if (null == standalone)
   standalone = headerEvent.isStandalone() ? "yes" : "no";
  StartDocument header = getXMLEventFactory()
  	.createStartDocument(encoding, version, "yes".equals(standalone));
  return header;
 }

 protected void handleFatalError(TransformerException ex)
 	throws TransformerException
 {
  try
  {
   getErrorListener().fatalError(ex);
  }
  catch (TransformerException e)
  {
   lastThrown = e;
   throw e;
  }
 }

 protected void handleError(TransformerException ex)
 	throws TransformerException
 {
  try
  {
   getErrorListener().error(ex);
  }
  catch (TransformerException e)
  {
   lastThrown = e;
   throw e;
  }
 }

 protected void handleWarning(TransformerException ex)
 	throws TransformerException
 {
  try
  {
   getErrorListener().warning(ex);
  }
  catch (TransformerException e)
  {
   lastThrown = e;
   throw e;
  }
 }

 protected TransformerException transformerExceptionFrom(XMLStreamException e)
 {
  SourceLocatorForLocation locator = null == e.getLocation()
  	? null : new SourceLocatorForLocation(e.getLocation());
  TransformerException ex = new TransformerException(e.getLocalizedMessage(), locator, e);
  return ex;
 }

 /**
  * Changes or removes the transformed document's header in accordance
  * with the transformer's {@link #getOutputProperties() output properties}.
  * All other events pass through this processor intact.
  * @see XMLEventTransformer#shapeXMLHeader(StartDocument)  
  */
 protected class XMLHeaderProcessor extends IdentityEventProcessor
 {
  @Override
  public void add(XMLEvent event) throws XMLStreamException
  {
   switch(event.getEventType())
   {
   case XMLEvent.START_DOCUMENT:
    if (!("yes".equals(getOutputProperty(OutputKeys.OMIT_XML_DECLARATION))))
    {
     event = shapeXMLHeader((StartDocument)event);
     super.add(event);
    }
    break;
   default:
    super.add(event);
   }
  }
 }

 private void handleStreamCloseFailure(XMLStreamException failure)
   throws TransformerException
 {
  try
  {
   handleError(transformerExceptionFrom(failure));
  }
  catch (TransformerException abort)
  {
   if (null != lastThrown)
    throw abort;
  }
 }

 // TODO: reset any instance vars used in transformation in reset() method
 private Throwable lastThrown;

 private XMLEventFactory eventFactory;
 private XMLEventProcessor transformer;
}
