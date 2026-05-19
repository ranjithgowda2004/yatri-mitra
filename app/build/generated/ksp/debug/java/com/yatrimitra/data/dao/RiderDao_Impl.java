package com.yatrimitra.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.yatrimitra.data.entity.RiderEntity;
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
public final class RiderDao_Impl implements RiderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RiderEntity> __insertionAdapterOfRiderEntity;

  private final EntityDeletionOrUpdateAdapter<RiderEntity> __deletionAdapterOfRiderEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExpired;

  public RiderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRiderEntity = new EntityInsertionAdapter<RiderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `riders` (`riderId`,`name`,`stopId`,`stopName`,`registeredAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiderEntity entity) {
        statement.bindString(1, entity.getRiderId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getStopId());
        statement.bindString(4, entity.getStopName());
        statement.bindLong(5, entity.getRegisteredAt());
      }
    };
    this.__deletionAdapterOfRiderEntity = new EntityDeletionOrUpdateAdapter<RiderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `riders` WHERE `riderId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RiderEntity entity) {
        statement.bindString(1, entity.getRiderId());
      }
    };
    this.__preparedStmtOfDeleteExpired = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM riders WHERE registeredAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RiderEntity rider, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRiderEntity.insert(rider);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final RiderEntity rider, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRiderEntity.handle(rider);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExpired(final long cutoff, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExpired.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoff);
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
          __preparedStmtOfDeleteExpired.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RiderEntity>> observeAtStop(final String stopId) {
    final String _sql = "SELECT * FROM riders WHERE stopId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, stopId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"riders"}, new Callable<List<RiderEntity>>() {
      @Override
      @NonNull
      public List<RiderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStopId = CursorUtil.getColumnIndexOrThrow(_cursor, "stopId");
          final int _cursorIndexOfStopName = CursorUtil.getColumnIndexOrThrow(_cursor, "stopName");
          final int _cursorIndexOfRegisteredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "registeredAt");
          final List<RiderEntity> _result = new ArrayList<RiderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiderEntity _item;
            final String _tmpRiderId;
            _tmpRiderId = _cursor.getString(_cursorIndexOfRiderId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStopId;
            _tmpStopId = _cursor.getString(_cursorIndexOfStopId);
            final String _tmpStopName;
            _tmpStopName = _cursor.getString(_cursorIndexOfStopName);
            final long _tmpRegisteredAt;
            _tmpRegisteredAt = _cursor.getLong(_cursorIndexOfRegisteredAt);
            _item = new RiderEntity(_tmpRiderId,_tmpName,_tmpStopId,_tmpStopName,_tmpRegisteredAt);
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
  public Object getAtStop(final String stopId,
      final Continuation<? super List<RiderEntity>> $completion) {
    final String _sql = "SELECT * FROM riders WHERE stopId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, stopId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RiderEntity>>() {
      @Override
      @NonNull
      public List<RiderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfRiderId = CursorUtil.getColumnIndexOrThrow(_cursor, "riderId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStopId = CursorUtil.getColumnIndexOrThrow(_cursor, "stopId");
          final int _cursorIndexOfStopName = CursorUtil.getColumnIndexOrThrow(_cursor, "stopName");
          final int _cursorIndexOfRegisteredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "registeredAt");
          final List<RiderEntity> _result = new ArrayList<RiderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RiderEntity _item;
            final String _tmpRiderId;
            _tmpRiderId = _cursor.getString(_cursorIndexOfRiderId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpStopId;
            _tmpStopId = _cursor.getString(_cursorIndexOfStopId);
            final String _tmpStopName;
            _tmpStopName = _cursor.getString(_cursorIndexOfStopName);
            final long _tmpRegisteredAt;
            _tmpRegisteredAt = _cursor.getLong(_cursorIndexOfRegisteredAt);
            _item = new RiderEntity(_tmpRiderId,_tmpName,_tmpStopId,_tmpStopName,_tmpRegisteredAt);
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
