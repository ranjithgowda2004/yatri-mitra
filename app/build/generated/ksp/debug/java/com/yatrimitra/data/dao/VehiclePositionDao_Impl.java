package com.yatrimitra.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.yatrimitra.data.entity.VehiclePositionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VehiclePositionDao_Impl implements VehiclePositionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VehiclePositionEntity> __insertionAdapterOfVehiclePositionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByVehicle;

  private final SharedSQLiteStatement __preparedStmtOfDeleteStale;

  public VehiclePositionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVehiclePositionEntity = new EntityInsertionAdapter<VehiclePositionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vehicle_positions` (`vehicleId`,`routeId`,`position`,`speedKmh`,`updatedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VehiclePositionEntity entity) {
        statement.bindString(1, entity.getVehicleId());
        statement.bindString(2, entity.getRouteId());
        statement.bindDouble(3, entity.getPosition());
        statement.bindDouble(4, entity.getSpeedKmh());
        statement.bindLong(5, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfDeleteByVehicle = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vehicle_positions WHERE vehicleId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteStale = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM vehicle_positions WHERE updatedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final VehiclePositionEntity position,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVehiclePositionEntity.insert(position);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByVehicle(final String vehicleId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByVehicle.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, vehicleId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteByVehicle.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteStale(final long thresholdMs, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteStale.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, thresholdMs);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteStale.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VehiclePositionEntity>> observeByRoute(final String routeId) {
    final String _sql = "SELECT * FROM vehicle_positions WHERE routeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, routeId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vehicle_positions"}, new Callable<List<VehiclePositionEntity>>() {
      @Override
      @NonNull
      public List<VehiclePositionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "speedKmh");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<VehiclePositionEntity> _result = new ArrayList<VehiclePositionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehiclePositionEntity _item;
            final String _tmpVehicleId;
            _tmpVehicleId = _cursor.getString(_cursorIndexOfVehicleId);
            final String _tmpRouteId;
            _tmpRouteId = _cursor.getString(_cursorIndexOfRouteId);
            final float _tmpPosition;
            _tmpPosition = _cursor.getFloat(_cursorIndexOfPosition);
            final float _tmpSpeedKmh;
            _tmpSpeedKmh = _cursor.getFloat(_cursorIndexOfSpeedKmh);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new VehiclePositionEntity(_tmpVehicleId,_tmpRouteId,_tmpPosition,_tmpSpeedKmh,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByRoute(final String routeId,
      final Continuation<? super List<VehiclePositionEntity>> $completion) {
    final String _sql = "SELECT * FROM vehicle_positions WHERE routeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, routeId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VehiclePositionEntity>>() {
      @Override
      @NonNull
      public List<VehiclePositionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVehicleId = CursorUtil.getColumnIndexOrThrow(_cursor, "vehicleId");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
          final int _cursorIndexOfSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "speedKmh");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<VehiclePositionEntity> _result = new ArrayList<VehiclePositionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VehiclePositionEntity _item;
            final String _tmpVehicleId;
            _tmpVehicleId = _cursor.getString(_cursorIndexOfVehicleId);
            final String _tmpRouteId;
            _tmpRouteId = _cursor.getString(_cursorIndexOfRouteId);
            final float _tmpPosition;
            _tmpPosition = _cursor.getFloat(_cursorIndexOfPosition);
            final float _tmpSpeedKmh;
            _tmpSpeedKmh = _cursor.getFloat(_cursorIndexOfSpeedKmh);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new VehiclePositionEntity(_tmpVehicleId,_tmpRouteId,_tmpPosition,_tmpSpeedKmh,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
