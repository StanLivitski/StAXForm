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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;

/**
 * Implements common methods required from custom
 * {@link Transformer XML transformers}.
 */
public abstract class AbstractTransformer extends Transformer
{
 @Override
 public void clearParameters()
 {
  parameters = null;
 }

 @Override
 public void setParameter(String name, Object value)
 {
  if (null == value)
   throw new NullPointerException("Null value assigned to transformer parameter \"" + name + '"');
  if (null == parameters)
   parameters = new TreeMap<String, Object>();
  parameters.put(name, value);
 }

 @Override
 public Object getParameter(String name)
 {
  return null == parameters ? null : parameters.get(name);
 }

 @Override
 public void setOutputProperties(Properties oformat)
 {
  for (String name : oformat.stringPropertyNames())
   setOutputProperty(name, oformat.getProperty(name));
 }

 @Override
 public void setOutputProperty(String name, String value)
   throws IllegalArgumentException
 {
  // make sure that the name is supported or namespace-qualified
  if (!name.startsWith("{"))
  {
   if (!SUPPORTED_OUTPUT_PROPERTIES.contains(name))
    throw new IllegalArgumentException("Unsupported XML Transformer output property \"" + name + '"');
  }
  outputProperties.setProperty(name, value);
 }

 @Override
 public Properties getOutputProperties()
 {
  Properties properties = new Properties();
  properties.putAll(defaultOutputProperties);
  properties.putAll(outputProperties);
  return properties;
 }

 @Override
 public String getOutputProperty(String name) throws IllegalArgumentException
 {
  return outputProperties.getProperty(name);
 }

 @Override
 public URIResolver getURIResolver()
 {
  return resolver;
 }

 @Override
 public void setURIResolver(URIResolver resolver)
 {
  resolver = null;
 }

 @Override
 public ErrorListener getErrorListener()
 {
  return null == errorListener ? new DefaultErrorListener(this) : errorListener;
 }

 @Override
 public void setErrorListener(ErrorListener listener)
   throws IllegalArgumentException
 {
  if (null == listener)
   throw new IllegalArgumentException("Cannot set an ErrorListener to null in an XML Transformer");
  errorListener = listener;
 }

 public AbstractTransformer()
 {
 }

 public static final Set<String> SUPPORTED_OUTPUT_PROPERTIES = new HashSet<String>();
 {
  Class<?> keysClass = OutputKeys.class;
  try
  {
   for (Field field : keysClass.getFields())
   {
    if ((Modifier.STATIC | Modifier.FINAL) != (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL))
      || field.getType() != String.class)
     continue;
    SUPPORTED_OUTPUT_PROPERTIES.add((String)field.get(null));
   }
  }
  catch (Exception e)
  {
   throw new ExceptionInInitializerError(e);
  }
 }

 private ErrorListener errorListener;
 private URIResolver resolver;
 private Properties outputProperties, defaultOutputProperties;
 private Map<String, Object> parameters;

 {
  defaultOutputProperties = new Properties();
  defaultOutputProperties.setProperty(OutputKeys.METHOD, "xml");
  defaultOutputProperties.setProperty(OutputKeys.VERSION, "1.0");
  defaultOutputProperties.setProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "");
//  defaultOutputProperties.setProperty(OutputKeys.ENCODING, "UTF-8");
  defaultOutputProperties.setProperty(OutputKeys.INDENT, "no");
  defaultOutputProperties.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
  defaultOutputProperties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
  defaultOutputProperties.setProperty(OutputKeys.STANDALONE, "no");

  outputProperties = new Properties(defaultOutputProperties);
 }
}
