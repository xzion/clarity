package skadistats.clarity.parser.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skadistats.clarity.decoder.StringTableDecoder;
import skadistats.clarity.match.Match;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.model.StringTableEntry;
import skadistats.clarity.parser.Handler;
import skadistats.clarity.parser.HandlerHelper;
import skadistats.clarity.parser.RegisterHandler;

import com.dota2.proto.Netmessages.CSVCMsg_UpdateStringTable;

@RegisterHandler(CSVCMsg_UpdateStringTable.class)
public class SvcUpdateStringTableHandler implements Handler<CSVCMsg_UpdateStringTable> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    public void apply(int peekTick, CSVCMsg_UpdateStringTable message, Match match) {
        HandlerHelper.traceMessage(log, peekTick, message);
        StringTable table = match.getStringTables().forId(message.getTableId());
        List<StringTableEntry> changes = StringTableDecoder.decode(table, message.getStringData().toByteArray(), message.getNumChangedEntries());
        StringTableApplier a = StringTableApplier.forName(table.getName());
        for (StringTableEntry t : changes) {
            a.apply(match, table.getName(), t.getIndex(), t.getKey(), t.getValue());
        }
    }

}
