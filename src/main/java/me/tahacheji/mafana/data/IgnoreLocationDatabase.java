package me.tahacheji.mafana.data;

import me.TahaCheji.mysqlData.MySQL;
import me.TahaCheji.mysqlData.MysqlValue;
import me.TahaCheji.mysqlData.SQLGetter;
import me.tahacheji.mafana.processor.WorldBlockUtil;
import me.tahacheji.mafana.util.EncryptionUtil;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IgnoreLocationDatabase extends MySQL {

    SQLGetter sqlGetter = new SQLGetter(this);

    public IgnoreLocationDatabase() {
        super("162.254.145.231", "3306", "51252", "51252", "346a1ef0fc");
    }

    public void setIgnoredLocation(String id, List<Location> locationList) {
        UUID uuid = new EncryptionUtil().stringToUUID(id);
        if(sqlGetter.exists(uuid)) {
            sqlGetter.setString(new MysqlValue("LOCATIONS", new EncryptionUtil().stringToUUID(id), new WorldBlockUtil().compressLocationsToJson(locationList)));
        } else {
            sqlGetter.setString(new MysqlValue("ID", new EncryptionUtil().stringToUUID(id), id));
            sqlGetter.setString(new MysqlValue("LOCATIONS", new EncryptionUtil().stringToUUID(id), new WorldBlockUtil().compressLocationsToJson(locationList)));
        }
    }

    public List<Location> getIgnoredLocation(String id) {
        UUID uuid = new EncryptionUtil().stringToUUID(id);
        if(sqlGetter.exists(uuid)) {
            return new WorldBlockUtil().decompressJsonToLocations(sqlGetter.getString(uuid, new MysqlValue("LOCATIONS")));
        }
        return new ArrayList<>();
    }

    public List<String> getAllIDs() {
        try {
            return sqlGetter.getAllString(new MysqlValue("ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void connect() {
        super.connect();
        if (this.isConnected()) sqlGetter.createTable("ignore_locations",
                new MysqlValue("ID", ""),
                new MysqlValue("LOCATIONS", ""));
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }
}
