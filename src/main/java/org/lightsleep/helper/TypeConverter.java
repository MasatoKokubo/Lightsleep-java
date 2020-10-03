// TypeConverter.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.lightsleep.component.SqlString;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
 * <b>TypeConverter</b> class has two types (source and destination) and a function to perform data conversion.
 * <p>
 * This class has a static maps (key: a combination of source type and destination type, value: a <b>TypeConverter</b>object).
 * It is initialized with the contents of the following table at the time of class initialization.<br>
 * </p>
 *
 * <table class="additional">
 *   <caption><span>Registered contents of the TypeConverter map</span></caption>
 *   <tr><th colspan="2">Key: Data Types</th><th rowspan="2">Value: Conversion Function</th></tr>
 *   <tr><th>Source</th><th>Destination</th></tr>
 * 
 *   <tr><td>Byte          </td><td rowspan="8">Boolean</td>
 *     <td rowspan="7">
 *       <b>false</b> <span class="comment">if the source value is <b>0</b></span><br>
 *       <b>true</b> <span class="comment">if the source value is <b>1</b></span><br>
 *       <div class="warning">Throws a <b>ConvertException</b> otherwise</div>
 *     </td>
 *   </tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td>
 *     <td>
 *       <b>false</b> <span class="comment">if the source value is <b>'0'</b></span><br>
 *       <b>true</b> <span class="comment">if the source value is <b>'1'</b></span><br>
 *       <div class="warning">Throws a <b>ConvertException</b> otherwise</div>
 *     </td>
 *   </tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="8">Byte</td>
 *     <td>
 *       <b>0</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>1</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Short         </td>
 *     <td rowspan="7">
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>byte</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="8">Short</td>
 *     <td>
 *       <b>0</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>1</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Integer       </td>
 *     <td rowspan="6">
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>short</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="10">Integer</td>
 *     <td>
 *       <b>0</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>1</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Long          </td>
 *     <td rowspan="4">
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>int</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>java.util.Date</td>
 *     <td>
 *       <b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Integer</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the <b>long</b> value is out of <b>int</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>LocalTime     </td>
 *     <td>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Integer</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the <b>long</b> value is out of <b>int</b> range.</div>
 *     </td>
 * </tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="15">Long</td>
 *     <td>
 *       <b>0L</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>1L</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Float         </td>
 *     <td rowspan="3">
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>long</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Time, Timestamp)</span></td><td><b>source.getTime()</b></td></tr>
 *   <tr><td>LocalDate     </td><td><b>Date.valueOf(source).getTime()</b></td></tr>
 *   <tr><td>LocalTime     </td><td><b>Time.valueOf(source).getTime() + source.getNano() / 1_000_000</b></td></tr>
 *   <tr><td>LocalDateTime </td><td><b>Timestamp.valueOf(source).getTime()</b></td></tr>
 *   <tr><td>OffsetDateTime</td><td rowspan="2"><b>source.toInstant().toEpochMilli()</b></td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td><td><b>source.toEpochMilli()</b></td></tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="8">Float</td>
 *     <td>
 *       <b>0.0F</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>1.0F</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>Double        </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td></td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="8">Double</td>
 *     <td>
 *       <b>0.0D</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>1.0D</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>Float         </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td></td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="8">BigDecimal</td>
 *     <td>
 *       <b>BigDecimal.ZERO</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>BigDecimal.ONE</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>Float         </td><td></td></tr>
 *   <tr><td>Double        </td><td></td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 * 
 *   <tr><td>Boolean       </td><td rowspan="8">Character</td>
 *     <td>
 *       <b>'0'</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>'1'</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td>
 *     <td rowspan="5">
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>char</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 * 
 *   <tr><td rowspan="4">Enum</td>
 *     <td>Byte     </td>
 *     <td rowspan="2">
 *       <b>source.ordinal()</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of the destination type range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Short  </td></tr>
 *   <tr><td>Integer</td><td rowspan="2"><b>source.ordinal()</b></td></tr>
 *   <tr><td>Long   </td></tr>
 * 
 *   <tr><td>Integer       </td><td rowspan="4">java.util.Date</td><td><b>new java.util.Date((long)(int)source)</b></td></tr>
 *   <tr><td>Long          </td><td><b>new java.util.Date(source)</b></td></tr>
 *   <tr><td>BigDecimal    </td>
 *     <td>
 *       <b>BigDecimal</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>java.util.Date</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>long</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDate     </td><td><b>new java.util.Date(Date.valueOf(source).getTime())</b></td></tr>
 * 
 *   <tr><td>Integer       </td><td rowspan="5">Date<br>(java.sql.Date)</td><td><b>new Date((long)(int)source)</b></td></tr>
 *   <tr><td>Long          </td><td><b>new Date(source)</b></td></tr>
 *   <tr><td>BigDecimal    </td>
 *     <td>
 *       <b>BigDecimal</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Date</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>long</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Time, Timestamp)</span></td><td><b>new Date(source.getTime())</b></td></tr>
 *   <tr><td>LocalDate     </td><td><b>Date.valueOf(source)</b></td></tr>
 * 
 *   <tr><td>Integer       </td><td rowspan="5">Time</td><td><b>new Time((long)(int)source)</b></td></tr>
 *   <tr><td>Long          </td><td><b>new Time(source)</b></td></tr>
 *   <tr><td>BigDecimal    </td>
 *     <td>
 *      <b>BigDecimal</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Time</b>.<br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>long</b> range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Timestamp)</span></td><td><b>new Time(source.getTime())</b></td></tr>
 *   <tr><td>LocalTime     </td><td><b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Time</b></td></tr>
 * 
 *   <tr><td>Long          </td><td rowspan="8">Timestamp</td><td><b>new Timestamp((long)(int)source)</b></td></tr>
 *   <tr><td>Integer       </td><td><b>new Timestamp(source)</b></td></tr>
 *   <tr><td>BigDecimal    </td>
 *     <td>
 *       <b>BigDecimal</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Timestamp</b>.<br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the source value is out of <b>long</b>range.</div>
 *     </td>
 *   </tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Time)</span></td><td><b>new Timestamp(source.getTime())</b></td></tr>
 *   <tr><td>LocalDateTime </td><td><b>Timestamp.valueOf(source)</b></td></tr>
 *   <tr><td>OffsetDateTime</td><td rowspan="2"><b>Timestamp.valueOf(source.toLocalDateTime())</b></td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td>
 *     <td>
 *       <b>Timestamp timestamp = new Timestamp(source.toEpochMilli());</b><br>
 *       <b>timestamp.setNanos(source.getNano());</b>
 *     </td>
 *   </tr>
 * 
 *   <tr><td>Long          </td><td rowspan="8">LocalDateTime</td><td><b>new Timestamp(source).toLocalDateTime()</b></td></tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Time)</span></td><td><b>new Timestamp(source.getTime()).toLocalDateTime()</b></td></tr>
 *   <tr><td>Timestamp     </td><td><b>source.toLocalDateTime()</b></td></tr>
 *   <tr><td>LocalDate     </td><td><b>source.atStartOfDay()</b></td></tr>
 *   <tr><td>LocalTime     </td><td><b>source.atDate(LocalDate.of(1970, 1, 1))</b></td></tr>
 *   <tr><td>OffsetDateTime</td><td rowspan="2"><b>source.toLocalDateTime()</b></td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td><td><b>LocalDateTime.ofInstant(source, ZoneId.systemDefault())</b></td></tr>
 * 
 *   <tr><td>Long          </td><td rowspan="8">LocalDate</td><td><b>new Date(source).toLocalDate()</b></td></tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Time)</span></td><td><b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDate</b></td></tr>
 *   <tr><td>Date          </td><td><b>source.toLocalDate()</b></td></tr>
 *   <tr><td>Timestamp     </td><td><b>source.toLocalDateTime().toLocalDate()</b></td></tr>
 *   <tr><td>LocalDateTime </td><td rowspan="3"><b>source.toLocalDate()</b></td></tr>
 *   <tr><td>OffsetDateTime</td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td><td><b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDate</b></td></tr>
 * 
 *   <tr><td>Long          </td><td rowspan="7">LocalTime</td><td><b>new Timestamp(source).toLocalDateTime().toLocalTime()</b></td></tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Time)</span></td><td><b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalTime</b></td></tr>
 *   <tr><td>Timestamp     </td><td><b>source.toLocalDateTime().toLocalTime()</b></td></tr>
 *   <tr><td>LocalDateTime </td><td rowspan="3"><b>source.toLocalTime()</b></td></tr>
 *   <tr><td>OffsetDateTime</td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td><td><b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalTime</b></td></tr>
 * 
 *   <tr><td>Long          </td><td rowspan="8">OffsetDateTime</td><td rowspan="3"><b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>OffsetDateTime</b></td></tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Time)</span></td></tr>
 *   <tr><td>Timestamp     </td></tr>
 *   <tr><td>LocalDateTime </td><td><b>source.atZone(ZoneId.systemDefault()).toOffsetDateTime()</b></td></tr>
 *   <tr><td>LocalDate     </td><td rowspan="2"><b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>OffsetDateTime</b></td></tr>
 *   <tr><td>LocalTime     </td></tr>
 *   <tr><td>ZonedDateTime </td><td><b>source.toOffsetDateTime()</b></td></tr>
 *   <tr><td>Instant       </td><td><b>OffsetDateTime.ofInstant(source, ZoneId.systemDefault())</b></td></tr>
 * 
 *   <tr><td>Long          </td><td rowspan="8">ZonedDateTime</td><td rowspan="3"><b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>ZonedDateTime</b></td></tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Time)</span></td></tr>
 *   <tr><td>Timestamp     </td></tr>
 *   <tr><td>LocalDateTime </td><td><b>source.atZone(ZoneId.systemDefault())</b></td></tr>
 *   <tr><td>LocalDate     </td><td rowspan="2"><b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>ZonedDateTime</b></td></tr>
 *   <tr><td>LocalTime     </td></tr>
 *   <tr><td>OffsetDateTime</td><td><b>source.toZonedDateTime()</b></td></tr>
 *   <tr><td>Instant       </td><td><b>ZonedDateTime.ofInstant(source, ZoneId.systemDefault())</b></td></tr>
 * 
 *   <tr><td>Long          </td><td rowspan="8">Instant</td><td><b>Instant.ofEpochMilli(source)</b></td></tr>
 *   <tr><td>java.util.Date<br><span class="comment">(Date, Time)</span></td><td><b>Instant.ofEpochMilli(source.getTime())</b></td></tr>
 *   <tr><td>Timestamp     </td><td><b>Instant.ofEpochSecond(source.getTime() / 1000L, (long)source.getNanos())</b></td></tr>
 *   <tr><td>LocalDateTime </td><td><b>source.toInstant(ZoneId.systemDefault().getRules().getOffset(source))</b></td></tr>
 *   <tr><td>LocalDate     </td><td rowspan="2"><b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Instant</b></td></tr>
 *   <tr><td>LocalTime     </td></tr>
 *   <tr><td>OffsetDateTime</td><td rowspan="2"><b>source.toInstant()</b></td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 * 
 *   <tr><td rowspan="19">String</td><td>Boolean</td>
 *     <td>
 *       <b>false</b> <span class="comment">if the source value is <b>"0"</b></span><br>
 *       <b>true</b> <span class="comment">if the source value is <b>"1"</b></span><br>
 *       <div class="warning">Throws a <b>ConvertException</b> otherwise</div>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td>
 *     <td rowspan="6">
 *       <b>&lt;destination class&gt;.valueOf(source)</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the <b>valueOf</b> method throws a <b>NumberFormatException</b>.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td>
 *     <td>
 *       <b>new BigDecimal(source)</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the <b>BigDecimal</b> constructor throws a <b>NumberFormatException</b>.</div>
 *     </td>
 *   </tr>
 *   <tr><td>Character     </td>
 *     <td>
 *       <b>source.charAt(0)</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the source length is not 1.</div>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDateTime </td>
 *     <td rowspan="6">
 *       <b>&lt;destination class&gt;.parse(source, <span class="comment">DateTimeFormatter object</span>)</b><br>
 *       <div class="warning">Throws a <b>ConvertException</b> if the <b>parse</b> method throws a <b>DateTimeParseException</b>.</div>
 *       <div class="comment">The format string of the <b>DateTimeFormatter</b> object is below.</div>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd HH:mm:ss</b>,<br>
 *       <b>yyyy-MM-dd HH:mm:ss.S</b><code>,...</code> or<br>
 *       <b>yyyy-MM-dd HH:mm:ss.SSSSSSSSS</b> <span class="comment">if convert to <b>LocalDateTime</b> or <b>Timestamp</b></span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd</b> <span class="comment">if convert to <b>LocalDate</b> or <b>Date</b></span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>HH:mm:ss</b>,<br>
 *       <b>HH:mm:ss.S</b><code>,...</code> or<br>
 *       <b>HH:mm:ss.SSSSSSSSS</b> <span class="comment">if convert to <b>LocalTime</b> or <b>Time</b></span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd HH:mm:ssxxx</b>,<br>
 *       <b>yyyy-MM-dd HH:mm:ss.Sxxx</b><code>,...</code> or<br>
 *       <b>yyyy-MM-dd HH:mm:ss.SSSSSSSSSxxx</b> <span class="comment">if convert to <b>OffsetDateTime</b></span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd HH:mm:ss[ ]VV</b>,<br>
 *       <b>yyyy-MM-dd HH:mm:ss.S[ ]VV</b><code>,...</code> or<br>
 *       <b>yyyy-MM-dd HH:mm:ss.SSSSSSSSS[ ]VV</b> <span class="comment">if convert to <b>ZonedDateTime</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDate     </td></tr>
 *   <tr><td>LocalTime     </td></tr>
 *   <tr><td>OffsetDateTime</td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td></tr>
 *   <tr><td>java.util.Date</td><td><b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>java.util.Date</b></td></tr>
 *   <tr><td>Date          </td><td><b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Date</b></td></tr>
 *   <tr><td>Time          </td><td><b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Time</b></td></tr>
 *   <tr><td>Timestamp     </td><td><b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Timestamp</b></td></tr>
 * 
 *   <tr>
 *     <td>Object<br><span class="comment">(Boolean, Byte,<br>Short, Integer,<br>Long, Float,<br>Double, Character<br>, Enum, ...)</span></td>
 *     <td rowspan="12">String</td><td><b>source.toString()</b></td>
 *   </tr>
 *   <tr><td>BigDecimal    </td><td><b>source.toPlainString()</b></td></tr>
 *   <tr><td>LocalDateTime </td>
 *     <td rowspan="5">
 *       <b>source.format(<span class="comment">DateTimeFormatter object</span>)</b>
 *       <div class="comment">The format string of <b>DateTimeFormatter</b> object is described above.</div>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDate     </td></tr>
 *   <tr><td>LocalTime     </td></tr>
 *   <tr><td>OffsetDateTime</td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td><td><b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>OffsetDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b></td></tr>
 *   <tr><td>java.uitl.Date</td><td><b>java.uitl.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b></td></tr>
 *   <tr><td>Date          </td><td><b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b></td></tr>
 *   <tr><td>Time          </td><td><b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b></td></tr>
 *   <tr><td>Timestamp     </td><td><b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b></td></tr>
 * </table>
 *
 * @since 1.0
 * @author Masato Kokubo
 * @see org.lightsleep.database.Standard
 * @see org.lightsleep.database.Db2
 * @see org.lightsleep.database.MariaDB
 * @see org.lightsleep.database.MySQL
 * @see org.lightsleep.database.Oracle
 * @see org.lightsleep.database.PostgreSQL
 * @see org.lightsleep.database.SQLite
 * @see org.lightsleep.database.SQLServer
 */
//public class TypeConverter<ST, DT> {
public class TypeConverter<ST, DT> {
    // The logger
    private static final Logger logger = LoggerFactory.getLogger(TypeConverter.class);

    // Well known classes
    private static final Set<Class<?>> wellKnownClasses = new HashSet<>();
    static {
        wellKnownClasses.add(Boolean.class);
        wellKnownClasses.add(Character.class);
        wellKnownClasses.add(Byte.class);
        wellKnownClasses.add(Short.class);
        wellKnownClasses.add(Integer.class);
        wellKnownClasses.add(Long.class);
        wellKnownClasses.add(Float.class);
        wellKnownClasses.add(Double.class);
        wellKnownClasses.add(BigDecimal.class);
        wellKnownClasses.add(String.class);
        wellKnownClasses.add(Date.class);
        wellKnownClasses.add(Time.class);
        wellKnownClasses.add(Timestamp.class);

        wellKnownClasses.add(LocalDate.class); // since 3.0.0
        wellKnownClasses.add(LocalTime.class); // since 3.0.0
        wellKnownClasses.add(LocalDateTime.class); // since 3.0.0
        wellKnownClasses.add(OffsetDateTime.class); // since 3.0.0
        wellKnownClasses.add(ZonedDateTime.class); // since 3.0.0
        wellKnownClasses.add(Instant.class); // since 3.0.0

        wellKnownClasses.add(BigDecimal[].class);
        wellKnownClasses.add(String[].class);
        wellKnownClasses.add(Date[].class);
        wellKnownClasses.add(Time[].class);
        wellKnownClasses.add(Timestamp[].class);

        wellKnownClasses.add(LocalDate[].class); // since 3.0.0
        wellKnownClasses.add(LocalTime[].class); // since 3.0.0
        wellKnownClasses.add(LocalDateTime[].class); // since 3.0.0
        wellKnownClasses.add(OffsetDateTime[].class); // since 3.0.0
        wellKnownClasses.add(ZonedDateTime[].class); // since 3.0.0
        wellKnownClasses.add(Instant[].class); // since 3.0.0

        wellKnownClasses.add(Clob.class);
        wellKnownClasses.add(Blob.class);
        wellKnownClasses.add(Enum.class);
        wellKnownClasses.add(Array.class);
        wellKnownClasses.add(Iterable.class);
        wellKnownClasses.add(ArrayList.class);
        wellKnownClasses.add(LinkedList.class);
        wellKnownClasses.add(HashSet.class);
        wellKnownClasses.add(LinkedHashSet.class);
        wellKnownClasses.add(TreeSet.class);
        wellKnownClasses.add(SqlString.class);
    }

    // The LocalDate and Date formatter (since 3.0.0)
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // The LocalTime and Time (since 3.0.0)
    private static final DateTimeFormatter[] timeFormatters = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("HH:mm:ss"),
        DateTimeFormatter.ofPattern("HH:mm:ss.S"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SS"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSS"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSS"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSS"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSS"),
        DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS")
    };

    // The LocalDateTime and Timestamp formatter (since 3.0.0)
    private static final DateTimeFormatter[] dateTimeFormatters = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")
    };

    // The OffsetDateTime formatter (since 3.0.0)
    private static final DateTimeFormatter[] offsetDateTimeFormatters = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.Sxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSxxx"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSxxx")
    };

    // The ZonedDateTime formatter (since 3.0.0)
    private static final DateTimeFormatter[] zonedDateTimeFormatters = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSS[ ]VV"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS[ ]VV")
    };

    // Returns the index of the time, date and time or date and time with offset formatter. (since 3.0.0)
    private static int getFormatterIndex(String string) {
        int minLength = "HH:mm:ss".length();
        int dotIndex = string.indexOf('.', minLength);
        if (minLength < dotIndex)
            minLength = dotIndex;
        int offsetIndex = string.indexOf('+', minLength);
        if (offsetIndex < 0) {
            offsetIndex = string.indexOf('-', minLength);
            if (offsetIndex < 0) {
                offsetIndex = string.indexOf(' ', minLength);
                if (offsetIndex < 0)
                    offsetIndex = string.length();
            }
        }
        int index = dotIndex < 0 ? 0 : offsetIndex - (dotIndex + 1);
        if (index >= timeFormatters.length)
            index = timeFormatters.length - 1;
        return index;
    }

    // Returns the index of the time, date and time or date and time with offset formatter. (since 3.0.0)
    private static int getFormatterIndex(int nano) {
        return
            nano               == 0 ? 0 :
            nano % 100_000_000 == 0 ? 1 :
            nano %  10_000_000 == 0 ? 2 :
            nano %   1_000_000 == 0 ? 3 :
            nano %     100_000 == 0 ? 4 :
            nano %      10_000 == 0 ? 5 :
            nano %       1_000 == 0 ? 6 :
            nano %         100 == 0 ? 7 :
            nano %          10 == 0 ? 8 : 9;
    }

    // The TypeConverter map
    private static final Map<String, TypeConverter<?, ?>> typeConverterMap = new ConcurrentHashMap<>();

    // The type of the source
    private final Class<ST> sourceType;

    // The type of the destination
    private final Class<DT> destinType;

    // The function for converting
    private final Function<? super ST, ? extends DT> function;

    // The key when stored in the map
    private final String key;

    // The hash code of this object
    private final int hashCode;

    /**
     * Creates and returns a key of the map from the destination and type of the source.
     *
     * @param sourceType the class of the source type
     * @param destinType the class of the destination type
     * @return the key
     *
     * @throws NullPointerException <b>sourceType</b> or <b>destinType</b> is <b>null</b>
     */
    public static String key(Class<?> sourceType, Class<?> destinType) {
        sourceType = Utils.toClassType(Objects.requireNonNull(sourceType, "sourceType is null"));
        destinType = Utils.toClassType(Objects.requireNonNull(destinType, "destinType is null"));

        String sourceTypeName = wellKnownClasses.contains(sourceType)
            ? sourceType.getSimpleName() : sourceType.getCanonicalName();
        String destinTypeName = wellKnownClasses.contains(destinType)
            ? destinType.getSimpleName() : destinType.getCanonicalName();

        String key = sourceTypeName + "->" + destinTypeName;
        return key;
    }

    /**
     * Puts the <b>typeConverter</b> in the <b>typeConverterMap</b>.
     *
     * @param typeConverterMap the <b>TypeConverter</b> map
     * @param typeConverter the <b>TypeConverter</b>
     *
     * @throws NullPointerException <b>typeConverterMap</b> or <b>typeConverter</b> is <b>null</b>
     */
    public static void put(Map<String, TypeConverter<?, ?>> typeConverterMap, TypeConverter<?, ?> typeConverter) {
        Objects.requireNonNull(typeConverterMap, "typeConverterMap is null");
        Objects.requireNonNull(typeConverter, "typeConverter is null");

        TypeConverter<?, ?> beforeTypeConverter = typeConverterMap.put(typeConverter.key, typeConverter);
        logger.debug(() -> "put: " + typeConverter + (beforeTypeConverter != null ? " (overwrite)" : ""));
    }

    /**
     * Finds and returns a <b>TypeConverter</b>
     * to convert <b>sourceType</b> to <b>destinType</b> in <b>typeConverterMap</b>.<br>
     *
     * If can not find <b>TypeConverter</b> to match with <b>sourceType</b> and <b>destinType</b>,
     * finds a <b>TypeConverter</b> to match
     * with interfaces of <b>sourceType</b> and <b>destinType</b>.<br>
     *
     * If still can not find,
     * finds a <b>TypeConverter</b> to match
     * with super classes of <b>sourceType</b> and <b>destinType</b>.<br>
     *
     * If still can not find, returns null<br>.
     *
     * If found with in the super class or interface,
     * puts them in <b>typeConverterMap</b> to be found directly next time.
     *
     * @param <ST> the type of the source
     * @param <DT> the type of the destination
     * @param typeConverterMap the <b>TypeConverter</b> map
     * @param sourceType the class of the source type
     * @param destinType the class of the destination type
     * @return a <b>TypeConverter</b>
     *
     * @throws NullPointerException <b>typeConverterMap</b>, s<b>ourceType</b> or <b>destinType</b> is <b>null</b>
     */
    public static <ST, DT> TypeConverter<ST, DT> get(Map<String, TypeConverter<?, ?>> typeConverterMap,
            Class<ST> sourceType, Class<DT> destinType) {
        Objects.requireNonNull(typeConverterMap, "typeConverterMap is null");

        String key = TypeConverter.key(sourceType, destinType);
        @SuppressWarnings("unchecked")
        TypeConverter<ST, DT> typeConverter = (TypeConverter<ST, DT>)typeConverterMap.get(key);

        if (typeConverter == null) {
            // can not find
            TypeConverter<ST, DT> typeConverter2 = search(typeConverterMap, sourceType, destinType);

            if (typeConverter2 != null) {
                // found
                TypeConverter<ST, DT> typeConverter3 = new TypeConverter<>(sourceType, destinType, typeConverter2.function());
                typeConverterMap.put(key, typeConverter3);

                logger.info(() -> "put: " + typeConverter3 + " (key: " + key + ")");

                typeConverter = typeConverter3;
            }
        }

        if (typeConverter == null)
        // 4.0.0
        //    logger.error("get: search("+ TypeConverter.key(sourceType, destinType) + ") -> not found"
            throw new IllegalArgumentException("get: search("+ TypeConverter.key(sourceType, destinType) + ") -> not found"
        ////
                + ", sourceType: " + sourceType.getCanonicalName()
                + ", destinType: " + destinType.getCanonicalName()
                );

        return typeConverter;
    }

    /**
     * Finds and returns a <b>TypeConverter</b>
     * to convert <b>sourceType</b> to <b>destinType</b> in <b>typeConverterMap</b>.<br>
     *
     * If can not find <b>TypeConverter</b> to match with <b>sourceType</b> and <b>destinType</b>,
     * finds a <b>TypeConverter</b> to match
     * with interfaces of <b>sourceType</b> and <b>destinType</b>.<br>
     *
     * If still can not find,
     * finds a <b>TypeConverter</b> to match
     * with super classes of <b>sourceType</b> and <b>destinType</b>.<br>
     *
     * If still can not find, returns <b>null</b><br>.
     *
     * @param <ST> the type of the source
     * @param <DT> the type of the destination
     * @param typeConverterMap the <b>TypeConverter</b> map
     * @param sourceType the class of the source type
     * @param destinType the class of the destination type
     * @return a <b>TypeConverter</b>
     *
     * @throws NullPointerException <b>typeConverterMap</b>, s<b>ourceType</b> or <b>destinType</b> is <b>null</b>
     */
    private static <ST, DT> TypeConverter<ST, DT> search(
            Map<String, TypeConverter<?, ?>> typeConverterMap,
            Class<? super ST> sourceType, Class<? extends DT> destinType) {
        logger.debug(() ->
            "search: sourceType: " + Utils.toLogString(sourceType)
            + ", destinType: " + Utils.toLogString(destinType));

        String key = TypeConverter.key(sourceType, destinType);
        @SuppressWarnings("unchecked")
        TypeConverter<ST, DT> typeConverter = (TypeConverter<ST, DT>)typeConverterMap.get(key);

        if (typeConverter == null) {
            // can not find
            // trys with interfaces of the source class
            @SuppressWarnings("unchecked")
            Class<? super ST>[] sourceInterfaces = (Class<? super ST>[])sourceType.getInterfaces();
            for (Class<? super ST> sourceInterface : sourceInterfaces) {
                typeConverter = search(typeConverterMap, sourceInterface, destinType);
                if (typeConverter != null)
                    break;
            }
        }

        if (typeConverter == null) {
            // can not find
            // trys with super classes of the source class
            Class<? super ST> sourceSuperType = sourceType.getSuperclass();
            if (sourceSuperType != null)
                typeConverter = search(typeConverterMap, sourceSuperType, destinType);
        }

        return typeConverter;
    }

    /**
     * If <b>source == null</b>, returns null<br>
     *
     * Otherwise if <b>destinType.isInstance(source)</b>,
     * Returns <b>source</b> without converting.
     *
     * Otherwise if found a <b>TypeConverter</b>,
     * Returns an object converted the source by the converter.
     *
     * @param <ST> the type of the source
     * @param <DT> the type of the destination
     * @param typeConverterMap the <b>TypeConverter</b> map
     * @param source the source object (permit null)
     * @param destinType the class of the destination type type (other than primitive types)
     * @return a converted object (might be null)
     *
     * @throws NullPointerException if <b>typeConverterMap</b> or <b>destinType</b> is <b>null</b>
     * @throws ConvertException if can not find the converter or the accuracy is lowered in the conversion
     * @throws IllegalArgumentException if <b>destinType</b> is a primitive type
     */
    public static <ST, DT> DT convert(Map<String, TypeConverter<?, ?>> typeConverterMap, ST source, Class<DT> destinType) {
        Objects.requireNonNull(typeConverterMap, "typeConverterMap is null");
        if (Objects.requireNonNull(destinType, "destinType is null").isPrimitive())
            throw new IllegalArgumentException("destinType: " + destinType.getName() + "(primitive type)");

        DT destin = null;
        if (source == null) {
            logger.debug(() -> "convert: null -> null");
        } else {
            if (destinType.isInstance(source)) {
                logger.debug(() -> "convert: " + toString(typeConverterMap, source)
                    + " -> cast to " + Utils.toLogString(destinType));
                destin = destinType.cast(source);
            } else {
                @SuppressWarnings("unchecked")
                Class<ST> sourceType = (Class<ST>)source.getClass();
                TypeConverter<ST, DT> typeConverter = get(typeConverterMap, sourceType, destinType);
                if (typeConverter == null) {
                    ConvertException e = new ConvertException(sourceType, source, destinType);
                    logger.error("convert: " + toString(typeConverterMap, source)
                        + " -> " + Utils.toLogString(destinType), e);
                    throw e;
                }

                destin = typeConverter.apply(source);

                if (logger.isDebugEnabled())
                    logger.debug("convert: converter: " + typeConverter.key
                        + ", conversion: " + toString(typeConverterMap, source)
                        + " -> " + toString(typeConverterMap, destin));
            }
        }

        return destin;
    }

    /**
     * Creates a new <b>TypeConverter</b> that converts <b>sourceType</b> to <b>destinType</b>.
     *
     * @param <ST> the type of the source
     * @param <DT> the type of the destination
     * @param sourceType the class of the source type
     * @param destinType the class of the destination type
     * @param function the function that the source type convert to the destination type
     * @return a new <b>TypeConverter</b> that converts <b>sourceType</b> to <b>destinType</b>
     *
     * @throws NullPointerException if <b>sourceType</b>, <b>destinType</b> or <b>function</b> is <b>null</b>
     *
     * @since 4.0.0
     */
    public static <ST, DT> TypeConverter<ST, DT> of(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, ? extends DT> function) {
        return new TypeConverter<ST, DT>(sourceType, destinType, function);
    }

    /**
     * Creates a new <b>TypeConverter</b> that converts <b>sourceType</b> to <b>middleType</b>
     * and converts it to <b>destinType</b>.
     *
     * @param <ST> the type of the source
     * @param <MT> the type of the middle
     * @param <DT> the type of the destination
     * @param typeConverterMap the typeConverter map
     * @param sourceType the class of the source type
     * @param middleType the class of the middle type
     * @param destinType the class of the destination type
     * @return a new <b>TypeConverter</b> that converts <b>sourceType</b> to <b>destinType</b>
     *
     * @throws NullPointerException if <b>typeConverterMap</b>, <b>sourceType</b>, <b>middleType</b> or <b>destinType</b> is <b>null</b>
     *
     * @since 4.0.0
     */
    public static <ST, MT, DT> TypeConverter<ST, DT> of(Map<String, TypeConverter<?, ?>> typeConverterMap,
            Class<ST> sourceType, Class<MT> middleType, Class<DT> destinType) {
        Function<? super ST, ? extends MT> function1 = get(typeConverterMap, sourceType, middleType).function();
        Function<? super MT, ? extends DT> function2 = get(typeConverterMap, middleType, destinType).function();
        return new TypeConverter<ST, DT>(sourceType, destinType, function1.andThen(function2));
    }

    /**
     * Creates a new <b>TypeConverter</b> that converts <b>sourceType</b> to <b>middleType</b>
     * and converts it to <b>destinType</b>.
     *
     * @param <ST> the type of the source
     * @param <MT> the type of the middle
     * @param <DT> the type of the destination
     * @param typeConverterMap the typeConverter map
     * @param sourceType the class of the source type
     * @param middleType the class of the middle type
     * @param destinType the class of the destination type
     * @param function the function that the middle type convert to the destination type
     * @return a new <b>TypeConverter</b> that converts <b>sourceType</b> to <b>destinType</b>
     *
     * @throws NullPointerException if <b>typeConverterMap</b>, <b>sourceType</b>, <b>middleType</b>, <b>destinType</b> or <b>function</b> is <b>null</b>
     *
     * @since 4.0.0
     */
    public static <ST, MT, DT> TypeConverter<ST, DT> of(Map<String, TypeConverter<?, ?>> typeConverterMap,
            Class<ST> sourceType, Class<MT> middleType, Class<DT> destinType, Function<? super MT, ? extends DT> function) {
        Function<? super ST, ? extends MT> function1 = get(typeConverterMap, sourceType, middleType).function();
        return new TypeConverter<ST, DT>(sourceType, destinType, function1.andThen(function));
    }

    /**
     * Returns a string representation of <b>source</b> using <b>typeConverterMap</b>.
     *
     * @param typeConverterMap the typeConverter map
     * @param source the target
     * @return a string representation of source
     */
    private static <ST> String toString(Map<String, TypeConverter<?, ?>> typeConverterMap, ST source) {
        if (source == null || source.getClass() == String.class)
            return Utils.toLogString(source);

        @SuppressWarnings("unchecked")
        TypeConverter<ST, String> typeConverter = get(typeConverterMap, (Class<ST>)source.getClass(), String.class);
        if (typeConverter == null)
            return Utils.toLogString(source);

        return typeConverter.apply(source);
    }

    /**
     * Returns an unmodifiable <b>TypeConverter</b> map
     * where various TypeConverter objects are registered.
     *
     * @return the unmodifiable <b>TypeConverter</b> map
     *
     * @since 1.8.1
     */
    public static Map<String, TypeConverter<?, ?>>typeConverterMap() {
        return Collections.unmodifiableMap(typeConverterMap);
    }

    /**
     * Constructs a new <b>TypeConverter</b>.
     *
     * @param sourceType the class of the source type
     * @param destinType the class of the destination type
     * @param function the function that the source type convert to the destination type
     *
     * @throws NullPointerException if <b>sourceType</b>, <b>destinType</b> or <b>function</b> is <b>null</b>
     */
    public TypeConverter(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, ? extends DT> function) {
        this.sourceType = Objects.requireNonNull(sourceType, "sourceType is null");
        this.destinType = Objects.requireNonNull(destinType, "destinType is null");
        this.function = Objects.requireNonNull(function, "function is null");
        key = key(sourceType, destinType);
        hashCode = key.hashCode();
    }

// 4.0.0,
//    /**
//     * Constructs a new <b>TypeConverter</b> by combining the two functions.
//     *
//     * @param <MT> the middle type of function 1 and function 2
//     * @param sourceType the class of the source type
//     * @param destinType the class of the destination type
//     * @param function1 the function that the source type convert to the middle type
//     * @param function2 the function that the middle type convert to the destination type
//     *
//     * @throws NullPointerException if <b>sourceType</b>, <b>destinType</b>, <b>function1</b> or <b>function2</b> is <b>null</b>
//     *
//     * @since 3.0.0
//     */
//    public <MT> TypeConverter(Class<ST> sourceType, Class<DT> destinType,
//            Function<? super ST, ? extends MT> function1, Function<? super MT, ? extends DT> function2) {
//        this(
//            sourceType,
//            destinType,
//            Objects.requireNonNull(function1, "function1 is null")
//                .andThen(Objects.requireNonNull(function2, "function2 is null"))
//        );
//    }
////

// 4.0.0,
//    /**
//     * Constructs a new <b>TypeConverter</b> by combining the three functions.
//     *
//     * @param <MT1> the middle type of function 1 and function 2
//     * @param <MT2> the middle type of function 2 and function 3
//     * @param sourceType the class of the source type
//     * @param destinType the class of the destination type
//     * @param function1 the function that the source type convert to the middle 1 type
//     * @param function2 the function that the middle 1 type convert to the middle 2 type
//     * @param function3 the function that the middle 2 type convert to the destination type
//     *
//     * @throws NullPointerException if <b>sourceType</b>, <b>destinType</b>, <b>function1</b>, <b>function2</b> or <b>function3</b> is <b>null</b>
//     *
//     * @since 3.0.0
//     */
//    public <MT1, MT2> TypeConverter(Class<ST> sourceType, Class<DT> destinType,
//            Function<? super ST, ? extends MT1> function1,
//            Function<? super MT1, ? extends MT2> function2,
//            Function<? super MT2, ? extends DT> function3) {
//        this(
//            sourceType,
//            destinType,
//            Objects.requireNonNull(function1, "function1 is null")
//                .andThen(Objects.requireNonNull(function2, "function2 is null"))
//                .andThen(Objects.requireNonNull(function3, "function3 is null"))
//        );
//    }
////

// 4.0.0,
//    /**
//     * Constructs a new <b>TypeConverter</b> by combining the four functions.
//     *
//     * @param <MT1> the middle type of function 1 and function 2
//     * @param <MT2> the middle type of function 2 and function 3
//     * @param <MT3> the middle type of function 3 and function 4
//     * @param sourceType the class of the source type
//     * @param destinType the class of the destination type
//     * @param function1 the function that the source type convert to the middle 1 type
//     * @param function2 the function that the middle 1 type convert to the middle 2 type
//     * @param function3 the function that the middle 2 type convert to the middle 3 type
//     * @param function4 the function that the middle 3 type convert to the destination type
//     *
//     * @throws NullPointerException if <b>sourceType</b>, <b>destinType</b>, <b>function1</b>, <b>function2</b>, <b>function3</b> or <b>function4</b> is <b>null</b>
//     *
//     * @since 3.0.0
//     */
//    public <MT1, MT2, MT3> TypeConverter(Class<ST> sourceType, Class<DT> destinType,
//            Function<? super ST, ? extends MT1> function1,
//            Function<? super MT1, ? extends MT2> function2,
//            Function<? super MT2, ? extends MT3> function3,
//            Function<? super MT3, ? extends DT> function4) {
//        this(
//            sourceType,
//            destinType,
//            Objects.requireNonNull(function1, "function1 is null")
//                .andThen(Objects.requireNonNull(function2, "function2 is null"))
//                .andThen(Objects.requireNonNull(function3, "function3 is null"))
//                .andThen(Objects.requireNonNull(function4, "function4 is null"))
//        );
//    }
////

    /**
     * Returns the type of the source.
     *
     * @return the type of the source
     */
    public Class<ST> sourceType() {
        return sourceType;
    }

    /**
     * Returns the type of the destination.
     *
     * @return the type of the destination
     */
    public Class<DT> destinType() {
        return destinType;
    }

    /**
     * Returns the function for converting.
     *
     * @return the function for converting
     */
    public Function<? super ST, ? extends DT> function() {
        return function;
    }

    /**
     * Returns the key.
     *
     * @return the key
     */
    public String key() {
        return key;
    }

    /**
     * Converts the data type of the value.
     *
     * @param value a source object
     * @return a converted object
     */
    public DT apply(ST value) {
        return function.apply(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof TypeConverter
            && sourceType == ((TypeConverter<?, ?>)object).sourceType
            && destinType == ((TypeConverter<?, ?>)object).destinType;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return key;
    }

    static {
    // * -> Boolean
        // Byte -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, Boolean.class, object -> {
                byte value = object;
                if (value == (byte)0) return false;
                if (value == (byte)1) return true;
                throw new ConvertException(Byte.class, object, Boolean.class);
            })
        );

        // Short -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(Short.class, Boolean.class, object -> {
                short value = object;
                if (value == (short)0) return false;
                if (value == (short)1) return true;
                throw new ConvertException(Short.class, object, Boolean.class);
            })
        );

        // Integer -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Boolean.class, object -> {
                int value = object;
                if (value == 0) return false;
                if (value == 1) return true;
                throw new ConvertException(Integer.class, object, Boolean.class);
            })
        );

        // Long -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Boolean.class, object -> {
                long value = object;
                if (value == 0L) return false;
                if (value == 1L) return true;
                throw new ConvertException(Long.class, object, Boolean.class);
            })
        );

        // Float -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(Float.class, Boolean.class, object -> {
                float value = object;
                if (value == 0.0F) return false;
                if (value == 1.0F) return true;
                throw new ConvertException(Float.class, object, Boolean.class);
            })
        );

        // Double -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(Double.class, Boolean.class, object -> {
                double value = object;
                if (value == 0.0D) return false;
                if (value == 1.0D) return true;
                throw new ConvertException(Double.class, object, Boolean.class);
            })
        );

        // BigDecimal -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Boolean.class, object -> {
                BigDecimal bigDecimal = object;
                if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return false;
                if (bigDecimal.compareTo(BigDecimal.ONE ) == 0) return true;
                throw new ConvertException(BigDecimal.class, object, Boolean.class);
            })
        );

        // Character -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(Character.class, Boolean.class, object -> {
                char value = object;
                if (value == '0') return false;
                if (value == '1') return true;
                throw new ConvertException(Character.class, object, Boolean.class);
            })
        );

    // * -> Byte
        // Boolean -> Byte
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, Byte.class, object -> object ? (byte)1 : (byte)0)
        );

        // Short -> Byte
        put(typeConverterMap,
            new TypeConverter<>(Short.class, Byte.class, object -> {
                short value = object;
                if ((short)(byte)value != value)
                    throw new ConvertException(Short.class, object, Byte.class, (byte)value);
                return (byte)value;
            })
        );

        // Integer -> Byte
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Byte.class, object -> {
                int value = object;
                if ((int)(byte)value != value)
                    throw new ConvertException(Integer.class, object, Byte.class, (byte)value);
                return (byte)value;
            })
        );

        // Long -> Byte
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Byte.class, object -> {
                long value = object;
                if ((long)(byte)value != value)
                    throw new ConvertException(Long.class, object, Byte.class, (byte)value);
                return (byte)value;
            })
        );

        // Float -> Byte
        put(typeConverterMap,
            new TypeConverter<>(Float.class, Byte.class, object -> {
                float value = object;
                if ((float)(byte)value != value)
                    throw new ConvertException(Float.class, object, Byte.class, (byte)value);
                return (byte)value;
            })
        );

        // Double -> Byte
        put(typeConverterMap,
            new TypeConverter<>(Double.class, Byte.class, object -> {
                double value = object;
                if ((double)(byte)value != value)
                    throw new ConvertException(Double.class, object, Byte.class, (byte)value);
                return (byte)value;
            })
        );

        // BigDecimal -> Byte
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Byte.class, object -> {
                try {
                    return object.byteValueExact();
                }
                catch (ArithmeticException e) {
                    throw new ConvertException(BigDecimal.class, object, Byte.class, null, e);
                }
            })
        );

        // Character -> Byte
        put(typeConverterMap,
            new TypeConverter<>(Character.class, Byte.class, object -> {
                char value = object;
                if ((char)(byte)value != value)
                    throw new ConvertException(Character.class, object, Byte.class, (byte)value);
                return (byte)value;
            })
        );

    // * -> Short
        // Boolean -> Short
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, Short.class, object -> object ? (short)1 : (short)0)
        );

        // Byte -> Short
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, Short.class, object -> (short)(byte)object)
        );

        // Integer -> Short
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Short.class, object -> {
                int value = object;
                if ((int)(short)value != value)
                    throw new ConvertException(Integer.class, object, Short.class);
                return (short)value;
            })
        );

        // Long -> Short
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Short.class, object -> {
                long value = object;
                if ((long)(short)value != value)
                    throw new ConvertException(Long.class, object, Short.class);
                return (short)value;
            })
        );

        // Float -> Short
        put(typeConverterMap,
            new TypeConverter<>(Float.class, Short.class, object -> {
                float value = object;
                if ((float)(short)value != value)
                    throw new ConvertException(Float.class, object, Short.class);
                return (short)value;
            })
        );

        // Double -> Short
        put(typeConverterMap,
            new TypeConverter<>(Double.class, Short.class, object -> {
                double value = object;
                if ((double)(short)value != value)
                    throw new ConvertException(Double.class, object, Short.class);
                return (short)value;
            })
        );

        // BigDecimal -> Short
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Short.class, object -> {
                try {
                    return object.shortValueExact();
                }
                catch (ArithmeticException e) {
                    throw new ConvertException(BigDecimal.class, object, Short.class, null, e);
                }
            })
        );

        // Character -> Short
        put(typeConverterMap,
            new TypeConverter<>(Character.class, Short.class, object -> (short)(char)object)
        );

    // * -> Integer
        // Boolean -> Integer
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, Integer.class, object -> object ? 1 : 0)
        );

        // Byte -> Integer
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, Integer.class, object -> (int)(byte)object)
        );

        // Short -> Integer
        put(typeConverterMap,
            new TypeConverter<>(Short.class, Integer.class, object -> (int)(short)object)
        );

        // Long -> Integer
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Integer.class, object -> {
                long value = object;
                if ((long)(int)value != value)
                    throw new ConvertException(Long.class, object, Integer.class, (int)value);
                return (int)value;
            })
        );

        // Float -> Integer
        put(typeConverterMap,
            new TypeConverter<>(Float.class, Integer.class, object -> {
                float value = object;
                if ((float)(int)value != value)
                    throw new ConvertException(Float.class, object, Integer.class, (int)value);
                return (int)value;
            })
        );

        // Double -> Integer
        put(typeConverterMap,
            new TypeConverter<>(Double.class, Integer.class, object -> {
                double value = object;
                if ((double)(int)value != value)
                    throw new ConvertException(Double.class, object, Integer.class, (int)value);
                return (int)value;
            })
        );

        // BigDecimal -> Integer
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Integer.class, object -> {
                try {
                    return object.intValueExact();
                }
                catch (ArithmeticException e) {
                    throw new ConvertException(BigDecimal.class, object, Integer.class, null, e);
                }
            })
        );

        // Character -> Integer
        put(typeConverterMap,
            new TypeConverter<>(Character.class, Integer.class, object -> (int)(char)object)
        );

    // * -> Long
        // Boolean -> Long
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, Long.class, object -> object ? 1L : 0L)
        );

        // Byte -> Long
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, Long.class, object -> (long)(byte)object)
        );

        // Short -> Long
        put(typeConverterMap,
            new TypeConverter<>(Short.class, Long.class, object -> (long)(short)object)
        );

        // Integer -> Long
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Long.class, object -> (long)(int)object)
        );

        // Float -> Long
        put(typeConverterMap,
            new TypeConverter<>(Float.class, Long.class, object -> {
                float value = object;
                if ((float)(long)value != value)
                    throw new ConvertException(Float.class, object, Long.class, (long)value);
                return (long)value;
            })
        );

        // Double -> Long
        put(typeConverterMap,
            new TypeConverter<>(Double.class, Long.class, object -> {
                double value = object;
                if ((double)(long)value != value)
                    throw new ConvertException(Double.class, object, Long.class, (long)value);
                return (long)value;
            })
        );

        // BigDecimal -> Long
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Long.class, object -> {
                try {
                    return object.longValueExact();
                }
                catch (ArithmeticException e) {
                    throw new ConvertException(BigDecimal.class, object, Long.class, null, e);
                }
            })
        );

        // Character -> Long
        put(typeConverterMap,
            new TypeConverter<>(Character.class, Long.class, object -> (long)(char)object)
        );

    // * -> Float
        // Boolean -> Float
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, Float.class, object -> object ? 1.0F : 0.0F)
        );

        // Byte -> Float
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, Float.class, object -> (float)(byte)object)
        );

        // Short -> Float
        put(typeConverterMap,
            new TypeConverter<>(Short.class, Float.class, object -> (float)(short)object)
        );

        // Integer -> Float
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Float.class, object -> (float)(int)object)
        );

        // Long -> Float
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Float.class, object -> (float)(long)object)
        );

        // Double -> Float
        put(typeConverterMap,
            new TypeConverter<>(Double.class, Float.class, object -> (float)(double)object)
        );

        // BigDecimal -> Float
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Float.class, object -> object.floatValue())
        );

        // Character -> Float
        put(typeConverterMap,
            new TypeConverter<>(Character.class, Float.class, object -> (float)(char)object)
        );

    // * -> Double
        // Boolean -> Double
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, Double.class, object -> object ? 1.0 : 0.0)
        );

        // Byte -> Double
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, Double.class, object -> (double)(byte)object)
        );

        // Short -> Double
        put(typeConverterMap,
            new TypeConverter<>(Short.class, Double.class, object -> (double)(short)object)
        );

        // Integer -> Double
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Double.class, object -> (double)(int)object)
        );

        // Long -> Double
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Double.class, object -> (double)(long)object)
        );

        // Float -> Double
        put(typeConverterMap,
            new TypeConverter<>(Float.class, Double.class, object -> (double)(float)object)
        );

        // BigDecimal -> Double
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Double.class, object -> object.doubleValue())
        );

        // Character -> Double
        put(typeConverterMap,
            new TypeConverter<>(Character.class, Double.class, object -> (double)(char)object)
        );

    // * -> BigDecimal
        // Boolean -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, BigDecimal.class, object -> object ? BigDecimal.ONE : BigDecimal.ZERO)
        );

        // Byte -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(byte)object))
        );

        // Short -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Short.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(short)object))
        );

        // Integer -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(int)object))
        );

        // Long -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Long.class, BigDecimal.class, object -> BigDecimal.valueOf(object))
        );

        // Float -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Float.class, BigDecimal.class, object -> BigDecimal.valueOf((double)(float)object))
        );

        // Double -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Double.class, BigDecimal.class, object -> BigDecimal.valueOf(object))
        );

        // Character -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(Character.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(char)object))
        );

    // * -> Character
        // Boolean -> Character
        put(typeConverterMap,
            new TypeConverter<>(Boolean.class, Character.class, object -> object ? '1' : '0')
        );

        // Byte -> Character
        put(typeConverterMap,
            new TypeConverter<>(Byte.class, Character.class, object -> (char)(byte)object)
        );

        // Short -> Character
        put(typeConverterMap,
            new TypeConverter<>(Short.class, Character.class, object -> (char)(short)object)
        );

        // Integer -> Character
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Character.class, object -> {
                int value = object;
                if ((int)(char)value != value)
                    throw new ConvertException(Integer.class, object, Character.class, (char)value);
                return (char)value;
            })
        );

        // Long -> Character
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Character.class, object -> {
                long value = object;
                if ((long)(char)value != value)
                    throw new ConvertException(Long.class, object, Character.class, (char)value);
                return (char)value;
            })
        );

        // Float -> Character
        put(typeConverterMap,
            new TypeConverter<>(Float.class, Character.class, object -> {
                float value = object;
                if ((float)(char)value != value)
                    throw new ConvertException(Float.class, object, Character.class, (char)value);
                return (char)value;
            })
        );

        // Double -> Character
        put(typeConverterMap,
            new TypeConverter<>(Double.class, Character.class, object -> {
                double value = object;
                if ((double)(char)value != value)
                    throw new ConvertException(Double.class, object, Character.class, (char)value);
                return (char)value;
            })
        );

        // BigDecimal -> Character
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, Character.class, object -> {
                try {
                    int value = object.intValueExact();
                    if (value < 0 || value > (int)'\uFFFF')
                        throw new ConvertException(BigDecimal.class, object, Character.class);
                    return (char)value;
                }
                catch (ArithmeticException e) {
                    throw new ConvertException(BigDecimal.class, object, Character.class, null, e);
                }
            })
        );

    // Enum -> *
        // Enum -> Integer (since 1.4.0)
        put(typeConverterMap,
            new TypeConverter<>(Enum.class, Integer.class, object -> object.ordinal())
        );

        // Enum -> Byte (since 1.4.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Enum.class, Byte.class,
        //        get(typeConverterMap, Enum.class, Integer.class).function,
        //        get(typeConverterMap, Integer.class, Byte.class).function
        //    )
            of(typeConverterMap, Enum.class, Integer.class, Byte.class)
        ////
        );

        // Enum -> Short (since 1.4.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Enum.class, Short.class,
        //        get(typeConverterMap, Enum.class, Integer.class).function,
        //        get(typeConverterMap, Integer.class, Short.class).function
        //    )
            of(typeConverterMap, Enum.class, Integer.class, Short.class)
        ////
        );

        // Enum -> Long (since 1.4.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Enum.class, Long.class,
        //        get(typeConverterMap, Enum.class, Integer.class).function,
        //        get(typeConverterMap, Integer.class, Long.class).function
        //    )
            of(typeConverterMap, Enum.class, Integer.class, Long.class)
        ////
        );

    // Date, Time, ... -> Long
        // java.util.Date -> Long (millisecond) (since 1.8.0)
        // (java.sql.Date -> Long)
        // (Time -> Long)
        // (Timestamp -> Long)
        put(typeConverterMap,
            new TypeConverter<>(java.util.Date.class, Long.class, object -> object.getTime())
        );

        // java.util.Date -> Integer (millisecond) (since 1.8.0)
        // (java.sql.Date -> Integer)
        // (Time -> Integer)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(java.util.Date.class, Integer.class,
        //        get(typeConverterMap, java.util.Date.class, Long.class).function,
        //        get(typeConverterMap, Long.class, Integer.class).function
        //    )
            of(typeConverterMap, java.util.Date.class, Long.class, Integer.class)
        ////
        );

        // LocalDate -> Long (millisecond) (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalDate.class, Long.class, object -> Date.valueOf(object).getTime())
        );

        // LocalTime -> Long (millisecond) (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalTime.class, Long.class, object ->
                Time.valueOf(object).getTime() + object.getNano() / 1_000_000
            )
        );

        // LocalTime -> Integer (millisecond) (since 3.0.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalTime.class, Integer.class,
        //        get(typeConverterMap, LocalTime.class, Long.class).function,
        //        get(typeConverterMap, Long.class, Integer.class).function
        //    )
            of(typeConverterMap, LocalTime.class, Long.class, Integer.class)
        ////
        );

        // LocalDateTime -> Long (millisecond) (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, Long.class, object -> Timestamp.valueOf(object).getTime())
        );

        // OffsetDateTime -> Long (millisecond) (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, Long.class, object -> object.toInstant().toEpochMilli())
        );

        // ZonedDateTime -> Long (millisecond) (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, Long.class, object -> object.toInstant().toEpochMilli())
        );

        // Instant -> Long (millisecond) (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(Instant.class, Long.class, object -> object.toEpochMilli())
        );

    // * -> java.util.Date (since 1.4.0)
        // Long -> java.util.Date
        put(typeConverterMap,
            new TypeConverter<>(Long.class, java.util.Date.class, object -> new java.util.Date(object))
        );

        // Integer -> java.util.Date (since 1.8.0)
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, java.util.Date.class, object -> new java.util.Date((long)(int)object))
        );

        // BigDecimal -> java.util.Date (since 1.8.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(BigDecimal.class, java.util.Date.class,
        //        get(typeConverterMap, BigDecimal.class, Long.class).function,
        //        get(typeConverterMap, Long.class, java.util.Date.class).function
        //    )
            of(typeConverterMap, BigDecimal.class, Long.class, java.util.Date.class)
        ////
        );

        // LocalDate -> java.util.Date (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalDate.class, java.util.Date.class, object ->
                new java.util.Date(Date.valueOf(object).getTime())
            )
        );

    // * -> java.sql.Date
        // Long -> java.sql.Date
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Date.class, object -> new Date(object))
        );

        // Integer -> java.sql.Date (since 1.8.0)
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Date.class, object -> new Date((long)(int)object))
        );

        // BigDecimal -> java.sql.Date (since 1.8.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(BigDecimal.class, Date.class,
        //        get(typeConverterMap, BigDecimal.class, Long.class).function,
        //        get(typeConverterMap, Long.class, Date.class).function
        //    )
            of(typeConverterMap, BigDecimal.class, Long.class, Date.class)
        ////
        );

        // java.util.Date -> java.sql.Date
        // (Time -> java.sql.Date)
        // (Timestamp -> java.sql.Date)
        put(typeConverterMap,
            new TypeConverter<>(java.util.Date.class, Date.class, object -> new Date(object.getTime()))
        );

        // LocalDate -> java.sql.Date (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalDate.class, Date.class, object -> Date.valueOf(object))
        );

    // * -> Time
        // Long -> Time
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Time.class, object -> new Time(object))
        );

        // Integer -> Time (since 1.8.0)
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Time.class, object -> new Time((long)(int)object))
        );

        // BigDecimal -> Time (since 1.8.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(BigDecimal.class, Time.class,
        //        get(typeConverterMap, BigDecimal.class, Long.class).function,
        //        get(typeConverterMap, Long.class, Time.class).function
        //    )
            of(typeConverterMap, BigDecimal.class, Long.class, Time.class)
        ////
        );

        // java.util.Date -> Time
        // (java.sql.Date -> Time)
        // (Timestamp -> Time)
        put(typeConverterMap,
            new TypeConverter<>(java.util.Date.class, Time.class, object -> new Time(object.getTime()))
        );

        // LocalTime -> Long -> Time (since 3.0.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalTime.class, Time.class,
        //        get(typeConverterMap, LocalTime.class, Long.class).function,
        //        get(typeConverterMap, Long.class, Time.class).function
        //    )
            of(typeConverterMap, LocalTime.class, Long.class, Time.class)
        ////
        );

    // * -> Timestamp
        // Long -> Timestamp
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Timestamp.class, object -> new Timestamp(object))
        );

        // Integer -> Timestamp (since 1.8.0)
        put(typeConverterMap,
            new TypeConverter<>(Integer.class, Timestamp.class, object -> new Timestamp((long)(int)object))
        );

        // BigDecimal -> Timestamp (since 1.8.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(BigDecimal.class, Timestamp.class,
        //        get(typeConverterMap, BigDecimal.class, Long.class).function,
        //        get(typeConverterMap, Long.class, Timestamp.class).function
        //    )
            of(typeConverterMap, BigDecimal.class, Long.class, Timestamp.class)
        ////
        );

        // java.util.Date -> Timestamp
        // (java.sql.Date -> Timestamp)
        // (Time -> Timestamp)
        put(typeConverterMap,
            new TypeConverter<>(java.util.Date.class, Timestamp.class, object -> new Timestamp(object.getTime()))
        );

        // LocalDateTime -> Timestamp (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, Timestamp.class, object -> Timestamp.valueOf(object))
        );

        // OffsetDateTime -> Timestamp (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, Timestamp.class, object ->
                Timestamp.valueOf(object.toLocalDateTime())
            )
        );

        // ZonedDateTime -> Timestamp (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, Timestamp.class, object ->
                Timestamp.valueOf(object.toLocalDateTime())
            )
        );

        // Instant -> Timestamp (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(Instant.class, Timestamp.class, object -> {
                Timestamp timestamp = new Timestamp(object.toEpochMilli());
                timestamp.setNanos(object.getNano());
                return timestamp;
            })
        );

    // * -> LocalDateTime (since 3.0.0)
        // Long -> LocalDateTime
        put(typeConverterMap,
            new TypeConverter<>(Long.class, LocalDateTime.class, object -> new Timestamp(object).toLocalDateTime())
        );

        // java.util.Date -> LocalDateTime
        // (java.sql.Date -> LocalDateTime)
        // (Time -> LocalDateTime)
        put(typeConverterMap,
            new TypeConverter<>(java.util.Date.class, LocalDateTime.class, object ->
                new Timestamp(object.getTime()).toLocalDateTime()
            )
        );

        // Timestamp -> LocalDateTime
        put(typeConverterMap,
            new TypeConverter<>(Timestamp.class, LocalDateTime.class, object -> object.toLocalDateTime())
        );

        // LocalDate -> LocalDateTime
        put(typeConverterMap,
            new TypeConverter<>(LocalDate.class, LocalDateTime.class, object -> object.atStartOfDay())
        );

        // LocalTime -> LocalDateTime
        put(typeConverterMap,
            new TypeConverter<>(LocalTime.class, LocalDateTime.class, object -> object.atDate(LocalDate.of(1970, 1, 1)))
        );

        // OffsetDateTime -> LocalDateTime
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, LocalDateTime.class, object -> object.toLocalDateTime())
        );

        // ZonedDateTime -> LocalDateTime
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, LocalDateTime.class, object -> object.toLocalDateTime())
        );

        // Instant -> LocalDateTime
        put(typeConverterMap,
            new TypeConverter<>(Instant.class, LocalDateTime.class, object ->
                LocalDateTime.ofInstant(object, ZoneId.systemDefault())
            )
        );

    // * -> LocalDate (since 3.0.0)
        // Long -> LocalDate
        put(typeConverterMap,
            new TypeConverter<>(Long.class, LocalDate.class, object -> new Date(object).toLocalDate())
        );

        // java.sql.Date -> LocalDate
        put(typeConverterMap,
            new TypeConverter<>(Date.class, LocalDate.class, object -> object.toLocalDate())
        );

        // java.util.Date -> java.sql.Date -> LocalDate
        // (Time -> java.sql.Date -> LocalDate)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(java.util.Date.class, LocalDate.class,
        //        get(typeConverterMap, java.util.Date.class, Date.class).function,
        //        get(typeConverterMap, Date.class, LocalDate.class).function
        //    )
            of(typeConverterMap, java.util.Date.class, Date.class, LocalDate.class)
        ////
        );

        // LocalTime -> LocalDate
        // None

        // LocalDateTime -> LocalDate
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, LocalDate.class, object -> object.toLocalDate())
        );

        // Timestamp -> LocalDate
        put(typeConverterMap,
            new TypeConverter<>(Timestamp.class, LocalDate.class, object -> object.toLocalDateTime().toLocalDate())
        );

        // OffsetDateTime -> LocalDate
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, LocalDate.class, object -> object.toLocalDate())
        );

        // ZonedDateTime -> LocalDate
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, LocalDate.class, object -> object.toLocalDate())
        );

        // Instant -> LocalDateTime -> LocalDate
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Instant.class, LocalDate.class,
        //        get(typeConverterMap, Instant.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, LocalDate.class).function
        //    )
            of(typeConverterMap, Instant.class, LocalDateTime.class, LocalDate.class)
        ////
        );

    // * -> LocalTime (since 3.0.0)
        // Long -> LocalTime
        put(typeConverterMap,
            new TypeConverter<>(Long.class, LocalTime.class, object ->
                new Timestamp(object).toLocalDateTime().toLocalTime()
            )
        );

        // Timestamp -> LocalTime
        put(typeConverterMap,
            new TypeConverter<>(Timestamp.class, LocalTime.class, object -> object.toLocalDateTime().toLocalTime())
        );

        // java.util.Date -> Timestamp -> LocalTime
        // (java.sql.Date -> Timestamp -> LocalTime)
        // (Time -> Timestamp -> LocalTime)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(java.util.Date.class, LocalTime.class,
        //        get(typeConverterMap, java.util.Date.class, Timestamp.class).function,
        //        get(typeConverterMap, Timestamp.class, LocalTime.class).function
        //    )
            of(typeConverterMap, java.util.Date.class, Timestamp.class, LocalTime.class)
        ////
        );

        // LocalDate -> LocalTime
        // None

        // LocalDateTime -> LocalTime
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, LocalTime.class, object -> object.toLocalTime())
        );

        // OffsetDateTime -> LocalTime
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, LocalTime.class, object -> object.toLocalTime())
        );

        // ZonedDateTime -> LocalTime
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, LocalTime.class, object -> object.toLocalTime())
        );

        // Instant -> LocalDateTime -> LocalTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Instant.class, LocalTime.class,
        //        get(typeConverterMap, Instant.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, LocalTime.class).function
        //    )
            of(typeConverterMap, Instant.class, LocalDateTime.class, LocalTime.class)
        ////
        );

    // * -> OffsetDateTime (since 3.0.0)
        // LocalDateTime -> OffsetDateTime
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, OffsetDateTime.class, object ->
                object.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            )
        );

        // Long -> LocalDateTime -> OffsetDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Long.class, OffsetDateTime.class,
        //        get(typeConverterMap, Long.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, OffsetDateTime.class).function
        //    )
            of(typeConverterMap, Long.class, LocalDateTime.class, OffsetDateTime.class)
        ////
        );

        // java.util.Date -> LocalDateTime -> OffsetDateTime
        // (java.sql.Date -> LocalDateTime -> OffsetDateTime)
        // (Time -> OffsetDateTime)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(java.util.Date.class, OffsetDateTime.class,
        //        get(typeConverterMap, java.util.Date.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, OffsetDateTime.class).function
        //    )
            of(typeConverterMap, java.util.Date.class, LocalDateTime.class, OffsetDateTime.class)
        ////
        );

        // Timestamp -> LocalDateTime -> OffsetDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Timestamp.class, OffsetDateTime.class,
        //        get(typeConverterMap, Timestamp.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, OffsetDateTime.class).function
        //    )
            of(typeConverterMap, Timestamp.class, LocalDateTime.class, OffsetDateTime.class)
        ////
        );

        // LocalDate -> LocalDateTime -> OffsetDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalDate.class, OffsetDateTime.class,
        //        get(typeConverterMap, LocalDate.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, OffsetDateTime.class).function
        //    )
            of(typeConverterMap, LocalDate.class, LocalDateTime.class, OffsetDateTime.class)
        ////
        );

        // LocalTime -> LocalDateTime -> OffsetDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalTime.class, OffsetDateTime.class,
        //        get(typeConverterMap, LocalTime.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, OffsetDateTime.class).function
        //    )
            of(typeConverterMap, LocalTime.class, LocalDateTime.class, OffsetDateTime.class)
        ////
        );

        // ZonedDateTime -> OffsetDateTime
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, OffsetDateTime.class, object -> object.toOffsetDateTime())
        );

        // Instant -> OffsetDateTime
        put(typeConverterMap,
            new TypeConverter<>(Instant.class, OffsetDateTime.class, object ->
                OffsetDateTime.ofInstant(object, ZoneId.systemDefault())
            )
        );

    // * -> ZonedDateTime (since 3.0.0)
        // LocalDateTime -> ZonedDateTime
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, ZonedDateTime.class, object -> object.atZone(ZoneId.systemDefault()))
        );

        // Long -> LocalDateTime -> ZonedDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Long.class, ZonedDateTime.class,
        //        get(typeConverterMap, Long.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, ZonedDateTime.class).function
            of(typeConverterMap, Long.class, LocalDateTime.class, ZonedDateTime.class)
        ////
        //    )
        );

        // java.util.Date -> LocalDateTime -> ZonedDateTime
        // (java.sql.Date -> LocalDateTime -> ZonedDateTime)
        // (Time -> LocalDateTime -> ZonedDateTime)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(java.util.Date.class, ZonedDateTime.class,
        //        get(typeConverterMap, java.util.Date.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, ZonedDateTime.class).function
            of(typeConverterMap, java.util.Date.class, LocalDateTime.class, ZonedDateTime.class)
        ////
        //    )
        );

        // Timestamp -> LocalDateTime -> ZonedDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Timestamp.class, ZonedDateTime.class,
        //        get(typeConverterMap, Timestamp.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, ZonedDateTime.class).function
            of(typeConverterMap, Timestamp.class, LocalDateTime.class, ZonedDateTime.class)
        ////
        //    )
        );

        // LocalDate -> LocalDateTime -> ZonedDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalDate.class, ZonedDateTime.class,
        //        get(typeConverterMap, LocalDate.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, ZonedDateTime.class).function
            of(typeConverterMap, LocalDate.class, LocalDateTime.class, ZonedDateTime.class)
        ////
        //    )
        );

        // LocalTime -> LocalDateTime -> ZonedDateTime
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalTime.class, ZonedDateTime.class,
        //        get(typeConverterMap, LocalTime.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, ZonedDateTime.class).function
            of(typeConverterMap, LocalTime.class, LocalDateTime.class, ZonedDateTime.class)
        ////
        //    )
        );

        // OffsetDateTime -> ZonedDateTime
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, ZonedDateTime.class, object -> object.toZonedDateTime())
        );

        // Instant -> ZonedDateTime
        put(typeConverterMap,
            new TypeConverter<>(Instant.class, ZonedDateTime.class, object ->
                ZonedDateTime.ofInstant(object, ZoneId.systemDefault())
            )
        );

    // * -> Instant (since 3.0.0)
        // Long -> Instant
        put(typeConverterMap,
            new TypeConverter<>(Long.class, Instant.class, object -> Instant.ofEpochMilli(object))
        );

        // java.util.Date -> Instant
        // (java.sql.Date -> Instant)
        // (Time -> Instant)
        put(typeConverterMap,
            new TypeConverter<>(java.util.Date.class, Instant.class, object -> Instant.ofEpochMilli(object.getTime()))
        );

        // Timestamp -> Instant
        put(typeConverterMap,
            new TypeConverter<>(Timestamp.class, Instant.class, object ->
                Instant.ofEpochSecond(object.getTime() / 1000L, (long)object.getNanos())
            )
        );

        // LocalDateTime -> Instant
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, Instant.class, object ->
                object.toInstant(ZoneId.systemDefault().getRules().getOffset(object))
            )
        );

        // LocalDate -> LocalDateTime -> Instant
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalDate.class, Instant.class,
        //        get(typeConverterMap, LocalDate.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, Instant.class).function
        //    )
            of(typeConverterMap, LocalDate.class, LocalDateTime.class, Instant.class)
        ////
        );

        // LocalTime -> LocalDateTime -> Instant
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(LocalTime.class, Instant.class,
        //        get(typeConverterMap, LocalTime.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, Instant.class).function
        //    )
            of(typeConverterMap, LocalTime.class, LocalDateTime.class, Instant.class)
        ////
        );

        // OffsetDateTime -> Instant
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, Instant.class, object -> object.toInstant())
        );

        // ZonedDateTime -> Instant
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, Instant.class, object -> object.toInstant())
        );

    // String -> *
        // String -> Boolean
        put(typeConverterMap,
            new TypeConverter<>(String.class, Boolean.class, object -> {
                if ("0".equals(object)) return false;
                if ("1".equals(object)) return true;
                throw new ConvertException(String.class, object, Boolean.class);
            })
        );

        // String -> Byte
        put(typeConverterMap,
            new TypeConverter<>(String.class, Byte.class, object -> {
                try {
                    return Byte.valueOf(object);
                }
                catch (NumberFormatException e) {
                    throw new ConvertException(String.class, object, Byte.class, null, e);
                }
            })
        );

        // String -> Short
        put(typeConverterMap,
            new TypeConverter<>(String.class, Short.class, object -> {
                try {
                    return Short.valueOf(object);
                }
                catch (NumberFormatException e) {
                    throw new ConvertException(String.class, object, Short.class, null, e);
                }
            })
        );

        // String -> Integer
        put(typeConverterMap,
            new TypeConverter<>(String.class, Integer.class, object -> {
                try {
                    return Integer.valueOf(object);
                }
                catch (NumberFormatException e) {
                    throw new ConvertException(String.class, object, Integer.class, null, e);
                }
            })
        );

        // String -> Long
        put(typeConverterMap,
            new TypeConverter<>(String.class, Long.class, object -> {
                try {
                    return Long.valueOf(object);
                }
                catch (NumberFormatException e) {
                    throw new ConvertException(String.class, object, Long.class, null, e);
                }
            })
        );

        // String -> Float
        put(typeConverterMap,
            new TypeConverter<>(String.class, Float.class, object -> {
                try {
                    return Float.valueOf(object);
                }
                catch (NumberFormatException e) {
                    throw new ConvertException(String.class, object, Float.class, null, e);
                }
            })
        );

        // String -> Double
        put(typeConverterMap,
            new TypeConverter<>(String.class, Double.class, object -> {
                try {
                    return Double.valueOf(object);
                }
                catch (NumberFormatException e) {
                    throw new ConvertException(String.class, object, Double.class, null, e);
                }
            })
        );

        // String -> BigDecimal
        put(typeConverterMap,
            new TypeConverter<>(String.class, BigDecimal.class, object -> {
                try {
                    return new BigDecimal(object);
                } catch (NumberFormatException e) {
                    throw new ConvertException(String.class, object, BigDecimal.class, null, e);
                }
            })
        );

        // String -> Character
        put(typeConverterMap,
            new TypeConverter<>(String.class, Character.class, object -> {
                if (object.length() != 1)
                    throw new ConvertException(String.class, object, Character.class);
                return object.charAt(0);
            })
        );

        // String -> LocalDateTime (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(String.class, LocalDateTime.class, object -> {
                try {
                    return LocalDateTime.parse(object, dateTimeFormatters[getFormatterIndex(object)]);
                }
                catch (DateTimeParseException e) {
                    throw new ConvertException(String.class, object, LocalDateTime.class, e);
                }
            })
        );

        // String -> LocalDate (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(String.class, LocalDate.class, object -> {
                try {
                    return LocalDate.parse(object, dateFormatter);
                }
                catch (DateTimeParseException e) {
                    throw new ConvertException(String.class, object, LocalDate.class, e);
                }
            })
        );

        // String -> LocalTime (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(String.class, LocalTime.class, object -> {
                try {
                    return LocalTime.parse(object, timeFormatters[getFormatterIndex(object)]);
                }
                catch (DateTimeParseException e) {
                    throw new ConvertException(String.class, object, LocalTime.class, e);
                }
            })
        );

        // String -> OffsetDateTime (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(String.class, OffsetDateTime.class, object -> {
                try {
                    return OffsetDateTime.parse(object, offsetDateTimeFormatters[getFormatterIndex(object)]);
                }
                catch (DateTimeParseException e) {
                    throw new ConvertException(String.class, object, OffsetDateTime.class, e);
                }
            })
        );

        // String -> ZonedDateTime (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(String.class, ZonedDateTime.class, object -> {
                try {
                    return ZonedDateTime.parse(object, zonedDateTimeFormatters[getFormatterIndex(object)]);
                }
                catch (DateTimeParseException e) {
                    throw new ConvertException(String.class, object, ZonedDateTime.class, e);
                }
            })
        );

        // String -> Instant (since 3.0.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(String.class, Instant.class,
        //        get(typeConverterMap, String.class, OffsetDateTime.class).function,
        //        get(typeConverterMap, OffsetDateTime.class, Instant.class).function
        //    )
            of(typeConverterMap, String.class, OffsetDateTime.class, Instant.class)
        ////
        );

        // String -> java.util.Date (since 1.4.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(String.class, java.util.Date.class,
        //        get(typeConverterMap, String.class, LocalDate.class).function,
        //        get(typeConverterMap, LocalDate.class, java.util.Date.class).function
        //    )
            of(typeConverterMap, String.class, LocalDate.class, java.util.Date.class)
        ////
        );

        // String -> java.sql.Date
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(String.class, Date.class,
        //        get(typeConverterMap, String.class, LocalDate.class).function,
        //        get(typeConverterMap, LocalDate.class, Date.class).function
        //    )
            of(typeConverterMap, String.class, LocalDate.class, Date.class)
        ////
        );

        // String -> Time
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(String.class, Time.class,
        //        get(typeConverterMap, String.class, LocalTime.class).function,
        //        get(typeConverterMap, LocalTime.class, Time.class).function
        //    )
            of(typeConverterMap, String.class, LocalTime.class, Time.class)
        ////
        );

        // String -> Timestamp
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(String.class, Timestamp.class,
        //        get(typeConverterMap, String.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, Timestamp.class).function
        //    )
            of(typeConverterMap, String.class, LocalDateTime.class, Timestamp.class)
        ////
        );

    // * -> String
        // Object -> String
        put(typeConverterMap,
            new TypeConverter<>(Object.class, String.class, object -> object.toString())
        );

        // BigDecimal -> String
        put(typeConverterMap,
            new TypeConverter<>(BigDecimal.class, String.class, object -> object.toPlainString())
        );

        // LocalDateTime -> String (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalDateTime.class, String.class, object ->
                object.format(dateTimeFormatters[getFormatterIndex(object.getNano())])
            )
        );

        // LocalDate -> String (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalDate.class, String.class, object -> object.format(dateFormatter))
        );

        // LocalTime -> String (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(LocalTime.class, String.class, object ->
                object.format(timeFormatters[getFormatterIndex(object.getNano())])
            )
        );

        // OffsetDateTime -> String (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(OffsetDateTime.class, String.class, object ->
                object.format(offsetDateTimeFormatters[getFormatterIndex(object.getNano())])
            )
        );

        // ZonedDateTime -> String (since 3.0.0)
        put(typeConverterMap,
            new TypeConverter<>(ZonedDateTime.class, String.class, object ->
                object.format(zonedDateTimeFormatters[getFormatterIndex(object.getNano())])
            )
        );

        // Instant -> String (since 3.0.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Instant.class, String.class,
        //        get(typeConverterMap, Instant.class, OffsetDateTime.class).function,
        //        get(typeConverterMap, OffsetDateTime.class, String.class).function
        //    )
            of(typeConverterMap, Instant.class, OffsetDateTime.class, String.class)
        ////
        );

        // java.util.Date -> String (since 1.4.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(java.util.Date.class, String.class,
        //        get(typeConverterMap, java.util.Date.class, LocalDate.class).function,
        //        get(typeConverterMap, LocalDate.class, String.class).function
        //    )
            of(typeConverterMap, java.util.Date.class, LocalDate.class, String.class)
        ////
        );

        // java.sql.Date -> String (since 1.4.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Date.class, String.class,
        //        get(typeConverterMap, Date.class, LocalDate.class).function,
        //        get(typeConverterMap, LocalDate.class, String.class).function
        //    )
            of(typeConverterMap, Date.class, LocalDate.class, String.class)
        ////
        );

        // Time -> String (since 1.4.0)
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Time.class, String.class,
        //        get(typeConverterMap, Time.class, LocalTime.class).function,
        //        get(typeConverterMap, LocalTime.class, String.class).function
        //    )
            of(typeConverterMap, Time.class, LocalTime.class, String.class)
        ////
        );

        // Timestamp -> String
        put(typeConverterMap,
        // 4.0.0
        //    new TypeConverter<>(Timestamp.class, String.class,
        //        get(typeConverterMap, Timestamp.class, LocalDateTime.class).function,
        //        get(typeConverterMap, LocalDateTime.class, String.class).function
        //    )
            of(typeConverterMap, Timestamp.class, LocalDateTime.class, String.class)
        ////
        );

    }
}
