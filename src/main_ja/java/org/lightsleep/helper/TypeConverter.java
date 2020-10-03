// TypeConverter.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.util.Map;
import java.util.function.Function;
import java.util.Objects;

/**
 * <b>TypeConverter</b>クラスは、2つの型(変換元および変換先)とデータ変換を行う関数を持ちます。
 * <p>
 * このクラスは静的なマップ(キー: 変換元型および変換先型を結合したもの, 値: <b>TypeConverter</b>オブジェクト)を持っており、
 * クラスの初期化時に以下の表の内容で初期化されます。<br>
 * </p>
 *
 * <table class="additional">
 *   <caption><span>TypeConverterマップへの登録内容</span></caption>
 *   <tr><th colspan="2">キー: データ型</th><th rowspan="2">値: 変換関数</th></tr>
 *   <tr><th>変換元</th><th>変換先</th></tr>
 *
 *   <tr><td>Byte          </td><td rowspan="8">Boolean</td>
 *     <td rowspan="7">
 *       <b>false</b> <span class="comment">変換元の値が<b>0</b>の場合</span><br>
 *       <b>true</b> <span class="comment">変換元の値が<b>1</b>の場合</span><br>
 *       <div class="warning">それ以外は<b>ConvertException</b>をスロー</div>
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
 *       <b>false</b> <span class="comment">変換元の値が<b>'0'</b>の場合</span><br>
 *       <b>true</b> <span class="comment">変換元の値が<b>'1'</b>の場合</span><br>
 *       <div class="warning">それ以外は<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="8">Byte</td>
 *     <td>
 *       <b>0</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>1</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>Short         </td>
 *     <td rowspan="7">
 *       <div class="warning">変換元の値が<b>byte</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <b>0</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>1</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Integer       </td>
 *     <td rowspan="6">
 *       <div class="warning">変換元の値が<b>short</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <b>0</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>1</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Long          </td>
 *     <td rowspan="4">
 *       <div class="warning">変換元の値が<b>int</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>java.util.Date</td>
 *     <td>
 *       <b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Integer</b><br>
 *       <div class="warning">long値が<b>int</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *   <tr><td>LocalTime     </td>
 *     <td>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Integer</b><br>
 *       <div class="warning">long値が<b>int</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="15">Long</td>
 *     <td>
 *       <b>0L</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>1L</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Float         </td>
 *     <td rowspan="3">
 *       <div class="warning">変換元の値が<b>long</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <b>0.0F</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>1.0F</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
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
 *       <b>0.0D</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>1.0D</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
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
 *       <b>BigDecimal.ZERO</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>BigDecimal.ONE</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
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
 *       <b>'0'</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>'1'</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td>
 *     <td rowspan="5">
 *       <div class="warning">変換元の値が<b>char</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <div class="warning">変換元の値が変換先型の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <div class="warning">変換元の値が<b>long</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDate     </td><td><b>new java.util.Date(Date.valueOf(source).getTime())</b></td></tr>
 *
 *   <tr><td>Integer       </td><td rowspan="5">Date<br>(java.sql.Date)</td><td><b>new Date((long)(int)source)</b></td></tr>
 *   <tr><td>Long          </td><td><b>new Date(source)</b></td></tr>
 *   <tr><td>BigDecimal    </td>
 *     <td>
 *       <b>BigDecimal</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Long</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>Date</b><br>
 *       <div class="warning">変換元の値が<b>long</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <div class="warning">変換元の値が<b>long</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <div class="warning">変換元の値が<b>long</b>の範囲外の場合は、<b>ConvertException</b>をスロー</div>
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
 *       <b>false</b> <span class="comment">変換元の値が<b>"0"</b>の場合</span><br>
 *       <b>true</b> <span class="comment">変換元の値が<b>"1"</b>の場合</span><br>
 *       <div class="warning">それ以外は<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *   <tr><td>Byte          </td>
 *     <td rowspan="6">
 *       <b>&lt;destination class&gt;.valueOf(source)</b><br>
 *       <div class="warning"><b>valueOf</b>メソッドが<b>NumberFormatException</b>をスローした場合は、<b>ConvertException</b>をスロー</div>
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
 *       <div class="warning"><b>BigDecimal</b>コンストラクタが<b>NumberFormatException</b>をスローした場合は、<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *   <tr><td>Character     </td>
 *     <td>
 *       <b>source.charAt(0)</b><br>
 *       <div class="warning">変換元の長さが<b>1</b>ではない場合は、<b>ConvertException</b>をスロー</div>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDateTime </td>
 *     <td rowspan="6">
 *       <b>&lt;destination class&gt;.parse(source, <span class="comment">DateTimeFormatterオブジェクト</span>)</b><br>
 *       <div class="warning"><b>parse</b>メソッドが<b>DateTimeParseException</b>をスローした場合は、<b>ConvertException</b>をスロー</div>
 *       <div class="comment"><b>DateTimeFormatter</b>のフォーマット文字列は以下</div>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd HH:mm:ss</b>,<br>
 *       <b>yyyy-MM-dd HH:mm:ss.S</b><code>,...</code> または<br>
 *       <b>yyyy-MM-dd HH:mm:ss.SSSSSSSSS</b> <span class="comment"><b>LocalDateTime</b>または<b>Timestamp</b>に変換する場合</span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd</b> <span class="comment"><b>LocalDate</b>または<b>Date</b>に変換する場合</span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>HH:mm:ss</b>,<br>
 *       <b>HH:mm:ss.S</b><code>,...</code> または<br>
 *       <b>HH:mm:ss.SSSSSSSSS</b> <span class="comment"><b>LocalTime</b>または<b>Time</b>に変換する場合</span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd HH:mm:ssxxx</b>,<br>
 *       <b>yyyy-MM-dd HH:mm:ss.Sxxx</b><code>,...</code> または<br>
 *       <b>yyyy-MM-dd HH:mm:ss.SSSSSSSSSxxx</b> <span class="comment"><b>OffsetDateTime</b>に変換する場合</span>
 *       <div class="blankline">&nbsp;</div>
 *       <b>yyyy-MM-dd HH:mm:ss[ ]VV</b>,<br>
 *       <b>yyyy-MM-dd HH:mm:ss.S[ ]VV</b><code>,...</code> または<br>
 *       <b>yyyy-MM-dd HH:mm:ss.SSSSSSSSS[ ]VV</b> <span class="comment"><b>ZonedDateTime</b>に変換する場合</span>
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
 *       <b>source.format(<span class="comment">DateTimeFormatterオブジェクト</span>)</b>
 *       <div class="comment"><b>DateTimeFormatter</b>のフォーマット文字列は上に記載</div>
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
 * @see org.lightsleep.database.MySQL
 * @see org.lightsleep.database.Oracle
 * @see org.lightsleep.database.PostgreSQL
 * @see org.lightsleep.database.SQLite
 * @see org.lightsleep.database.SQLServer
 */
public class TypeConverter<ST, DT> {
    /**
     * 変換元の型と変換先の型の組み合わせで、マップのキーとして使用する文字列を作成します。
     *
     * @param sourceType 変換元の型のクラス
     * @param destinType 変換先の型のクラス
     * @return キー
     *
     * @throws NullPointerException <b>sourceType</b> または<b>destinType</b>が<b>null</b>の場合
     */
    public static String key(Class<?> sourceType, Class<?> destinType) {
        return null;
    }

    /**
     * <b>TypeConverter</b>マップに<b>TypeConverter</b> 配列の各要素を関連付けます。
     *
     * @param typeConverterMap <b>TypeConverter</b>マップ
     * @param typeConverters <b>TypeConverter</b>オブジェクト配列
     *
     * @throws NullPointerException <b>typeConverterMap</b>, <b>typeConverters</b> または<b>typeConverters</b>の要素が<b>null</b>の場合
     */
    public static void put(Map<String, TypeConverter<?, ?>> typeConverterMap, TypeConverter<?, ?>... typeConverters) {
    }

    /**
     * <b>typeConverterMap</b>から
     * <b>sourceType</b>を<b>destinType</b>に変換する<b>TypeConverter</b>オブジェクトを返します。<br>
     *
     * <b>sourceType</b>と<b>destinType</b>の組み合わせでマッチする
     * <b>TypeConverter</b>オブジェクトが見つからない場合は、
     * <b>sourceType</b>のスーパークラスやインタフェースでマッチするのを探します。<br>
     *
     * それでも見つからない場合は、<b>null</b>を返します。<br>
     *
     * スーパークラスまたはインターフェースで見つかった場合は、次回は直接見つかるようにマップに登録します。<br>
     *
     * @param <ST> 変換元の型
     * @param <DT> 変換先の型
     * @param typeConverterMap <b>TypeConverter</b>マップ
     * @param sourceType 変換元の型のクラス
     * @param destinType 変換先の型のクラス
     * @return TypeConverterオブジェクト (見つからない場合はnull)
     *
     * @throws NullPointerException typeConverterMap, <b>sourceType</b> または<b>destinType</b>が<b>null</b>の場合
     */
    public static <ST, DT> TypeConverter<ST, DT> get(Map<String, TypeConverter<?, ?>> typeConverterMap,
            Class<ST> sourceType, Class<DT> destinType) {
        return null;
    }

    /**
     * <b>source</b> == null の場合は、null を返します。<br>
     * <b>destinType.isInstance(source)</b>の場合は、<b>source</b>を変換しないで返します。<br>
     * コンバータが見つかった場合は、そのコンバータで<b>source</b>を変換したオブジェクトを返します。
     *
     * @param <ST> 変換元の型
     * @param <DT> 変換先の型
     * @param typeConverterMap <b>TypeConverter</b>マップ
     * @param source 変換元のオブジェクト(null可)
     * @param destinType 変換先の型のクラス (プリミティブ型以外)
     * @return 型を変換されたオブジェクト(null有)
     *
     * @throws NullPointerException <b>typeConverterMap</b> または<b>destinType</b>が<b>null</b>の場合     *
     * @throws ConvertException コンバータが見つからない場合か変換処理で精度が落ちた場合
     * @throws IllegalArgumentException <b>destinType</b>がプリミティブタイプの場合
     */
    public static <ST, DT> DT convert(Map<String, TypeConverter<?, ?>> typeConverterMap, ST source, Class<DT> destinType) {
        return null;
    }

    /**
     * 変換元の型を変換先の型に変換する<b>TypeConverter</b>オブジェクトを作成します。
     *
     * @param <ST> 変換元の型
     * @param <DT> 変換先の型
     * @param sourceType 変換元の型のクラス
     * @param destinType 変換先の型のクラス
     * @param function 変換元の型を変換先の型に変換する関数
     * @return 変換元の型を変換先の型に変換する<b>TypeConverter</b>オブジェクト
     *
     * @throws NullPointerException <b>sourceType</b>, <b>destinType</b>または<b>function</b>が<b>null</b>の場合
     *
     * @since 4.0.0
     */
    public static <ST, DT> TypeConverter<ST, DT> of(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, ? extends DT> function) {
        return new TypeConverter<ST, DT>(sourceType, destinType, function);
    }

    /**
     * 変換元の型を中間の型に変換し、それを変換先の型に変換する<b>TypeConverter</b>オブジェクトを作成します。
     *
     * @param <ST> 変換元の型
     * @param <MT> 中間の型
     * @param <DT> 変換先の型
     * @param typeConverterMap <b>TypeConverter</b>マップ
     * @param sourceType 変換元の型のクラス
     * @param middleType 中間の型のクラス
     * @param destinType 変換先の型のクラス
     * @return 変換元の型を変換先の型に変換する<b>TypeConverter</b>オブジェクト
     *
     * @throws NullPointerException <b>typeConverterMap</b>, <b>sourceType</b>, <b>middleType</b>または<b>destinType</b>が<b>null</b>の場合
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
     * 変換元の型を中間の型に変換し、それを変換先の型に変換する<b>TypeConverter</b>オブジェクトを作成します。
     *
     * @param <ST> 変換元の型
     * @param <MT> 中間の型
     * @param <DT> 変換先の型
     * @param typeConverterMap <b>TypeConverter</b>マップ
     * @param sourceType 変換元の型のクラス
     * @param middleType 中間の型のクラス
     * @param destinType 変換先の型のクラス
     * @param function 中間の型を変換先の型に変換する関数
     * @return 変換元の型を変換先の型に変換する<b>TypeConverter</b>オブジェクト
     *
     * @throws NullPointerException <b>typeConverterMap</b>, <b>sourceType</b>, <b>middleType</b>, <b>destinType</b>または<b>function</b>が<b>null</b>の場合
     *
     * @since 4.0.0
     */
    public static <ST, MT, DT> TypeConverter<ST, DT> of(Map<String, TypeConverter<?, ?>> typeConverterMap,
            Class<ST> sourceType, Class<MT> middleType, Class<DT> destinType, Function<? super MT, ? extends DT> function) {
        Function<? super ST, ? extends MT> function1 = get(typeConverterMap, sourceType, middleType).function();
        return new TypeConverter<ST, DT>(sourceType, destinType, function1.andThen(function));
    }

    /**
     * TypeConverterオブジェクトが登録された変更不可な<b>TypeConverter</b>マップを返します。
     *
     * @return <b>TypeConverter</b>マップ
     *
     * @since 1.8.1
     */
    public static Map<String, TypeConverter<?, ?>>typeConverterMap() {
        return null;
    }

    /**
     * <b>TypeConverter</b>を構築します。
     *
     * @param sourceType 変換元の型のクラス
     * @param destinType 変換先の型のクラス
     * @param function データ変換を行う関数
     *
     * @throws NullPointerException <b>sourceType</b>, <b>destinType</b>または<b>function</b>が<b>null</b>の場合
     */
    public TypeConverter(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, ? extends DT> function) {
    }

// 4.0.0,
//    /**
//     * 2つの関数を結合して<b>TypeConverter</b>を構築します。
//     *
//     * @param <MT> 関数1と関数2の中間型
//     * @param sourceType 変換元の型のクラス
//     * @param destinType 変換先の型のクラス
//     * @param function1 データ変換を行う関数1
//     * @param function2 データ変換を行う関数2
//     *
//     * @throws NullPointerException <b>sourceType</b>, <b>destinType</b>, <b>function1</b>または<b>function2</b>が<b>null</b>の場合
//     *
//     * @since 3.0.0
//     */
//    public <MT> TypeConverter(Class<ST> sourceType, Class<DT> destinType,
//            Function<? super ST, MT> function1, Function<? super MT, ? extends DT> function2) {
//        this(
//            sourceType,
//            destinType,
//            Objects.requireNonNull(function1, "function1")
//                .andThen(Objects.requireNonNull(function2, "function2"))
//        );
//    }
////

// 4.0.0,
//    /**
//     * 3つの関数を結合して<b>TypeConverter</b>を構築します。
//     *
//     * @param <MT1> 関数1と関数2の中間型
//     * @param <MT2> 関数2と関数3の中間型
//     * @param sourceType 変換元の型のクラス
//     * @param destinType 変換先の型のクラス
//     * @param function1 データ変換を行う関数1
//     * @param function2 データ変換を行う関数2
//     * @param function3 データ変換を行う関数3
//     *
//     * @throws NullPointerException <b>sourceType</b>, <b>destinType</b>, <b>function1</b>, <b>function2</b>または<b>function3</b>が<b>null</b>の場合
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
//            Objects.requireNonNull(function1, "function1")
//                .andThen(Objects.requireNonNull(function2, "function2"))
//                .andThen(Objects.requireNonNull(function3, "function3"))
//        );
//    }
////

// 4.0.0,
//    /**
//     * 4つの関数を結合して<b>TypeConverter</b>を構築します。
//     *
//     * @param <MT1> 関数1と関数2の中間型
//     * @param <MT2> 関数2と関数3の中間型
//     * @param <MT3> 関数3と関数4の中間型
//     * @param sourceType 変換元の型のクラス
//     * @param destinType 変換先の型のクラス
//     * @param function1 データ変換を行う関数1
//     * @param function2 データ変換を行う関数2
//     * @param function3 データ変換を行う関数3
//     * @param function4 データ変換を行う関数4
//     *
//     * @throws NullPointerException <b>sourceType</b>, <b>destinType</b>, <b>function1</b>, <b>function2</b>, <b>function3</b>または<b>function4</b>が<b>null</b>の場合
//     *
//     * @since 3.0.0
//     */
//    public <MT1, MT2, MT3> TypeConverter(Class<ST> sourceType, Class<DT> destinType,
//            Function<? super ST, MT1> function1,
//            Function<? super MT1, ? extends MT2> function2,
//            Function<? super MT2, ? extends MT3> function3,
//            Function<? super MT3, ? extends DT> function4) {
//        this(
//            sourceType,
//            destinType,
//            Objects.requireNonNull(function1, "function1")
//                .andThen(Objects.requireNonNull(function2, "function3"))
//                .andThen(Objects.requireNonNull(function3, "function3"))
//                .andThen(Objects.requireNonNull(function4, "function4"))
//        );
//    }
////

    /**
     * 変換元の型を返します。
     *
     * @return 変換元の型
     */
    public Class<ST> sourceType() {
        return null;
    }

    /**
     * 変換先の型を返します。
     *
     * @return 変換先の型
     */
    public Class<DT> destinType() {
        return null;
    }

    /**
     * 型を変換する関数を返します。
     *
     * @return 型を変換する関数
     */
    public Function<? super ST, ? extends DT> function() {
        return null;
    }

    /**
     * キーを返します。
     *
     * @return キー
     */
    public String key() {
        return null;
    }

    /**
     * <b>value</b>の型を変換します。
     *
     * @param value 変換元のオブジェクト
     *
     * @return 変換されたオブジェクト
     */
    public DT apply(ST value) {
        return null;
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
