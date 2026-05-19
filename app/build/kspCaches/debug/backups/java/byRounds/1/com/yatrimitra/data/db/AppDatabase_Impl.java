package com.yatrimitra.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.yatrimitra.data.dao.ArrivalDao;
import com.yatrimitra.data.dao.ArrivalDao_Impl;
import com.yatrimitra.data.dao.RiderDao;
import com.yatrimitra.data.dao.RiderDao_Impl;
import com.yatrimitra.data.dao.RouteDao;
import com.yatrimitra.data.dao.RouteDao_Impl;
import com.yatrimitra.data.dao.StopDao;
import com.yatrimitra.data.dao.StopDao_Impl;
import com.yatrimitra.data.dao.VehicleDao;
import com.yatrimitra.data.dao.VehicleDao_Impl;
import com.yatrimitra.data.dao.VehiclePositionDao;
import com.yatrimitra.data.dao.VehiclePositionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RouteDao _routeDao;

  private volatile StopDao _stopDao;

  private volatile VehicleDao _vehicleDao;

  private volatile VehiclePositionDao _vehiclePositionDao;

  private volatile RiderDao _riderDao;

  private volatile ArrivalDao _arrivalDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `routes` (`routeId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `lengthKm` REAL NOT NULL, `colorHex` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`routeId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `stops` (`stopId` TEXT NOT NULL, `routeId` TEXT NOT NULL, `name` TEXT NOT NULL, `position` REAL NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, PRIMARY KEY(`stopId`), FOREIGN KEY(`routeId`) REFERENCES `routes`(`routeId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_stops_routeId` ON `stops` (`routeId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicles` (`vehicleId` TEXT NOT NULL, `routeId` TEXT, `driverName` TEXT NOT NULL, `capacity` INTEGER NOT NULL, `registeredAt` INTEGER NOT NULL, PRIMARY KEY(`vehicleId`), FOREIGN KEY(`routeId`) REFERENCES `routes`(`routeId`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vehicles_routeId` ON `vehicles` (`routeId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vehicle_positions` (`vehicleId` TEXT NOT NULL, `routeId` TEXT NOT NULL, `position` REAL NOT NULL, `speedKmh` REAL NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`vehicleId`), FOREIGN KEY(`vehicleId`) REFERENCES `vehicles`(`vehicleId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_vehicle_positions_vehicleId` ON `vehicle_positions` (`vehicleId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `riders` (`riderId` TEXT NOT NULL, `name` TEXT NOT NULL, `stopId` TEXT NOT NULL, `stopName` TEXT NOT NULL, `registeredAt` INTEGER NOT NULL, PRIMARY KEY(`riderId`), FOREIGN KEY(`stopId`) REFERENCES `stops`(`stopId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_riders_stopId` ON `riders` (`stopId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `arrivals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicleId` TEXT NOT NULL, `stopId` TEXT NOT NULL, `stopName` TEXT NOT NULL, `arrivedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4c2fbbd02b14a7bd5e42215d9ef4429f')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `routes`");
        db.execSQL("DROP TABLE IF EXISTS `stops`");
        db.execSQL("DROP TABLE IF EXISTS `vehicles`");
        db.execSQL("DROP TABLE IF EXISTS `vehicle_positions`");
        db.execSQL("DROP TABLE IF EXISTS `riders`");
        db.execSQL("DROP TABLE IF EXISTS `arrivals`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRoutes = new HashMap<String, TableInfo.Column>(7);
        _columnsRoutes.put("routeId", new TableInfo.Column("routeId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("lengthKm", new TableInfo.Column("lengthKm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("colorHex", new TableInfo.Column("colorHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoutes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRoutes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRoutes = new TableInfo("routes", _columnsRoutes, _foreignKeysRoutes, _indicesRoutes);
        final TableInfo _existingRoutes = TableInfo.read(db, "routes");
        if (!_infoRoutes.equals(_existingRoutes)) {
          return new RoomOpenHelper.ValidationResult(false, "routes(com.yatrimitra.data.entity.RouteEntity).\n"
                  + " Expected:\n" + _infoRoutes + "\n"
                  + " Found:\n" + _existingRoutes);
        }
        final HashMap<String, TableInfo.Column> _columnsStops = new HashMap<String, TableInfo.Column>(6);
        _columnsStops.put("stopId", new TableInfo.Column("stopId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStops.put("routeId", new TableInfo.Column("routeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStops.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStops.put("position", new TableInfo.Column("position", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStops.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStops.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStops = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysStops.add(new TableInfo.ForeignKey("routes", "CASCADE", "NO ACTION", Arrays.asList("routeId"), Arrays.asList("routeId")));
        final HashSet<TableInfo.Index> _indicesStops = new HashSet<TableInfo.Index>(1);
        _indicesStops.add(new TableInfo.Index("index_stops_routeId", false, Arrays.asList("routeId"), Arrays.asList("ASC")));
        final TableInfo _infoStops = new TableInfo("stops", _columnsStops, _foreignKeysStops, _indicesStops);
        final TableInfo _existingStops = TableInfo.read(db, "stops");
        if (!_infoStops.equals(_existingStops)) {
          return new RoomOpenHelper.ValidationResult(false, "stops(com.yatrimitra.data.entity.StopEntity).\n"
                  + " Expected:\n" + _infoStops + "\n"
                  + " Found:\n" + _existingStops);
        }
        final HashMap<String, TableInfo.Column> _columnsVehicles = new HashMap<String, TableInfo.Column>(5);
        _columnsVehicles.put("vehicleId", new TableInfo.Column("vehicleId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("routeId", new TableInfo.Column("routeId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("driverName", new TableInfo.Column("driverName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("capacity", new TableInfo.Column("capacity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehicles.put("registeredAt", new TableInfo.Column("registeredAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVehicles = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVehicles.add(new TableInfo.ForeignKey("routes", "SET NULL", "NO ACTION", Arrays.asList("routeId"), Arrays.asList("routeId")));
        final HashSet<TableInfo.Index> _indicesVehicles = new HashSet<TableInfo.Index>(1);
        _indicesVehicles.add(new TableInfo.Index("index_vehicles_routeId", false, Arrays.asList("routeId"), Arrays.asList("ASC")));
        final TableInfo _infoVehicles = new TableInfo("vehicles", _columnsVehicles, _foreignKeysVehicles, _indicesVehicles);
        final TableInfo _existingVehicles = TableInfo.read(db, "vehicles");
        if (!_infoVehicles.equals(_existingVehicles)) {
          return new RoomOpenHelper.ValidationResult(false, "vehicles(com.yatrimitra.data.entity.VehicleEntity).\n"
                  + " Expected:\n" + _infoVehicles + "\n"
                  + " Found:\n" + _existingVehicles);
        }
        final HashMap<String, TableInfo.Column> _columnsVehiclePositions = new HashMap<String, TableInfo.Column>(5);
        _columnsVehiclePositions.put("vehicleId", new TableInfo.Column("vehicleId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehiclePositions.put("routeId", new TableInfo.Column("routeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehiclePositions.put("position", new TableInfo.Column("position", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehiclePositions.put("speedKmh", new TableInfo.Column("speedKmh", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVehiclePositions.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVehiclePositions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVehiclePositions.add(new TableInfo.ForeignKey("vehicles", "CASCADE", "NO ACTION", Arrays.asList("vehicleId"), Arrays.asList("vehicleId")));
        final HashSet<TableInfo.Index> _indicesVehiclePositions = new HashSet<TableInfo.Index>(1);
        _indicesVehiclePositions.add(new TableInfo.Index("index_vehicle_positions_vehicleId", false, Arrays.asList("vehicleId"), Arrays.asList("ASC")));
        final TableInfo _infoVehiclePositions = new TableInfo("vehicle_positions", _columnsVehiclePositions, _foreignKeysVehiclePositions, _indicesVehiclePositions);
        final TableInfo _existingVehiclePositions = TableInfo.read(db, "vehicle_positions");
        if (!_infoVehiclePositions.equals(_existingVehiclePositions)) {
          return new RoomOpenHelper.ValidationResult(false, "vehicle_positions(com.yatrimitra.data.entity.VehiclePositionEntity).\n"
                  + " Expected:\n" + _infoVehiclePositions + "\n"
                  + " Found:\n" + _existingVehiclePositions);
        }
        final HashMap<String, TableInfo.Column> _columnsRiders = new HashMap<String, TableInfo.Column>(5);
        _columnsRiders.put("riderId", new TableInfo.Column("riderId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("stopId", new TableInfo.Column("stopId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("stopName", new TableInfo.Column("stopName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRiders.put("registeredAt", new TableInfo.Column("registeredAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRiders = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRiders.add(new TableInfo.ForeignKey("stops", "CASCADE", "NO ACTION", Arrays.asList("stopId"), Arrays.asList("stopId")));
        final HashSet<TableInfo.Index> _indicesRiders = new HashSet<TableInfo.Index>(1);
        _indicesRiders.add(new TableInfo.Index("index_riders_stopId", false, Arrays.asList("stopId"), Arrays.asList("ASC")));
        final TableInfo _infoRiders = new TableInfo("riders", _columnsRiders, _foreignKeysRiders, _indicesRiders);
        final TableInfo _existingRiders = TableInfo.read(db, "riders");
        if (!_infoRiders.equals(_existingRiders)) {
          return new RoomOpenHelper.ValidationResult(false, "riders(com.yatrimitra.data.entity.RiderEntity).\n"
                  + " Expected:\n" + _infoRiders + "\n"
                  + " Found:\n" + _existingRiders);
        }
        final HashMap<String, TableInfo.Column> _columnsArrivals = new HashMap<String, TableInfo.Column>(5);
        _columnsArrivals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArrivals.put("vehicleId", new TableInfo.Column("vehicleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArrivals.put("stopId", new TableInfo.Column("stopId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArrivals.put("stopName", new TableInfo.Column("stopName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsArrivals.put("arrivedAt", new TableInfo.Column("arrivedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysArrivals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesArrivals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoArrivals = new TableInfo("arrivals", _columnsArrivals, _foreignKeysArrivals, _indicesArrivals);
        final TableInfo _existingArrivals = TableInfo.read(db, "arrivals");
        if (!_infoArrivals.equals(_existingArrivals)) {
          return new RoomOpenHelper.ValidationResult(false, "arrivals(com.yatrimitra.data.entity.ArrivalEntity).\n"
                  + " Expected:\n" + _infoArrivals + "\n"
                  + " Found:\n" + _existingArrivals);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4c2fbbd02b14a7bd5e42215d9ef4429f", "ec5f0ea9e2e124666dc5f572dfd554e1");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "routes","stops","vehicles","vehicle_positions","riders","arrivals");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `routes`");
      _db.execSQL("DELETE FROM `stops`");
      _db.execSQL("DELETE FROM `vehicles`");
      _db.execSQL("DELETE FROM `vehicle_positions`");
      _db.execSQL("DELETE FROM `riders`");
      _db.execSQL("DELETE FROM `arrivals`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RouteDao.class, RouteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StopDao.class, StopDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VehicleDao.class, VehicleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VehiclePositionDao.class, VehiclePositionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RiderDao.class, RiderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ArrivalDao.class, ArrivalDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RouteDao routeDao() {
    if (_routeDao != null) {
      return _routeDao;
    } else {
      synchronized(this) {
        if(_routeDao == null) {
          _routeDao = new RouteDao_Impl(this);
        }
        return _routeDao;
      }
    }
  }

  @Override
  public StopDao stopDao() {
    if (_stopDao != null) {
      return _stopDao;
    } else {
      synchronized(this) {
        if(_stopDao == null) {
          _stopDao = new StopDao_Impl(this);
        }
        return _stopDao;
      }
    }
  }

  @Override
  public VehicleDao vehicleDao() {
    if (_vehicleDao != null) {
      return _vehicleDao;
    } else {
      synchronized(this) {
        if(_vehicleDao == null) {
          _vehicleDao = new VehicleDao_Impl(this);
        }
        return _vehicleDao;
      }
    }
  }

  @Override
  public VehiclePositionDao vehiclePositionDao() {
    if (_vehiclePositionDao != null) {
      return _vehiclePositionDao;
    } else {
      synchronized(this) {
        if(_vehiclePositionDao == null) {
          _vehiclePositionDao = new VehiclePositionDao_Impl(this);
        }
        return _vehiclePositionDao;
      }
    }
  }

  @Override
  public RiderDao riderDao() {
    if (_riderDao != null) {
      return _riderDao;
    } else {
      synchronized(this) {
        if(_riderDao == null) {
          _riderDao = new RiderDao_Impl(this);
        }
        return _riderDao;
      }
    }
  }

  @Override
  public ArrivalDao arrivalDao() {
    if (_arrivalDao != null) {
      return _arrivalDao;
    } else {
      synchronized(this) {
        if(_arrivalDao == null) {
          _arrivalDao = new ArrivalDao_Impl(this);
        }
        return _arrivalDao;
      }
    }
  }
}
