/**
 * EasySOA Registry
 * Copyright 2011-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.registry.rest.jackson;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.annotate.JsonValue;
import org.codehaus.jackson.map.util.StdDateFormat;


/**
 * Date wrapper that replaces Date or Calendar impls.
 * 
 * Uses a threaded Jackson StdDateFormat, which formats as ISO8001 date with GMT timezone.
 * 
 * It is in UTC (Greenwhich) timezone, which make hours "less readable" but is an otherwise
 * more correct solution (since timezone can't always be perfectly known, see
 * http://stackoverflow.com/questions/10570884/how-to-get-the-current-time-and-timezone-from-locale ).
 * An alternative would be to use a SimpleDateFormat with pattern "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
 * (ex. : 2013-07-31T11:08:28.693+02:00) and remove the final ':', see
 * http://stackoverflow.com/questions/2201925/converting-iso8601-compliant-string-to-java-util-date .
 * However the Jackson StdDateFormat is otherwise far more powerful (more lenient parsing).
 * 
 * This setup (Jackson StdDateFormat) could have been alternatively triggered by configuring ObjectMapper :
 * mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true)
 * or by enabling JAXB (!).
 * This object could also be easily modified to use a different DateFormat. Alternatively, this could
 * be done by configuring ObjectMapper :
 * mapper.getSerializationConfig().withDateFormat(new StdDateFormat())
 * or in Jackson 2+ by @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd,HH:00", timezone="CET")
 * However in both cases less configuration was preferred, and this solution also allows
 * to handle any Calendar impl (ex. GregorianCalendar) without specifying all of them among
 * @JsonSubTypes({ @JsonSubTypes.Type(GregorianCalendar.class)... of encompassing SoaListType
 * or SoaMapType.
 * 
 * see also :
 * http://wiki.fasterxml.com/JacksonFAQDateHandling
 * http://stackoverflow.com/questions/7556851/set-jackson-timezone-for-date-deserialization
 * http://loianegroner.com/2010/09/how-to-serialize-java-util-date-with-jackson-json-processor-spring-3-0/
 * http://jackson.codehaus.org/1.1.2/javadoc/org/codehaus/jackson/map/util/StdDateFormat.html
 * http://stackoverflow.com/questions/11230954/jax-rs-jackson-json-provider-date-format-issue
 * 
 * @author mdutoo
 *
 */
@JsonTypeName("date")
public class SoaDateType implements Serializable {
    
    private static final long serialVersionUID = 2171894440334987275L;

    private static ThreadLocal<StdDateFormat> STDDATEFORMAT = new ThreadLocal<StdDateFormat>();

    private Date value;

    public SoaDateType() {
        super();
    }

    /**
     * Creator for Jackson at deserialization
     * @param timestamp can be ISO8601, but also RFC-1123, see Jackson StdDateFormat
     * @throws ParseException
     */
    public SoaDateType(String timestamp) throws ParseException {
        this.value = getDateFormat().parse(timestamp);
    }
    
    public SoaDateType(Date value) {
        this.value = value;
    }

    /**
     * Jackson getter at serialization
     * 
     * @return ISO8601 formatted date
     * @throws ParseException
     */
    @JsonValue
    public String getISO8601Value() {
        return getDateFormat().format(this.getValue());
    }
    
    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }

    /**
     * @return threaded (though should be already) Jackson StdDateFormat
     * Creates it first and caches it if doesn't exist yet
     */
    public static StdDateFormat getDateFormat() {
        if (STDDATEFORMAT.get() == null) {
            StdDateFormat df = new StdDateFormat();
            ///df.setTimeZone(TimeZone.getDefault()); // NO NullPointerException
            STDDATEFORMAT.set(df);
        }
        return STDDATEFORMAT.get();
    }

}
