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

import javax.xml.stream.Location;
import javax.xml.transform.SourceLocator;

/**
 * Wraps a {@link Location} object into a {@link SourceLocator}.
 */
public class SourceLocatorForLocation implements SourceLocator
{
 public int getColumnNumber()
 {
  return location.getColumnNumber();
 }

 public int getLineNumber()
 {
  return location.getLineNumber();
 }

 public String getPublicId()
 {
  return location.getPublicId();
 }

 public String getSystemId()
 {
  return location.getSystemId();
 }

 public SourceLocatorForLocation(Location location)
 {
  this.location = location;
 }

 private Location location;
}
