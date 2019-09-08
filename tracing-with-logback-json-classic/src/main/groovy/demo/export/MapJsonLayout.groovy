package demo.export

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.contrib.json.classic.JsonLayout
import groovy.transform.CompileStatic

@CompileStatic
class MapJsonLayout extends JsonLayout {

    // Overriding toJsonMap instead of addCustomDataToJsonMap as most data is not needed
    @Override
    protected Map toJsonMap(ILoggingEvent event) {
        def map = super.toJsonMap(event)

        assert event.argumentArray.length == 1
        assert event.argumentArray[0] instanceof Map

        map.putAll((Map) event.argumentArray[0])

        return map
    }
}
