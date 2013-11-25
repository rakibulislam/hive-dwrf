package com.facebook.hive.orc.lazy;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.facebook.hive.orc.InStream;
import com.facebook.hive.orc.OrcProto;
import com.facebook.hive.orc.PositionProvider;
import com.facebook.hive.orc.RunLengthByteReader;
import com.facebook.hive.orc.StreamName;
import com.facebook.hive.orc.OrcProto.RowIndex;
import org.apache.hadoop.hive.serde2.io.ByteWritable;

public class LazyByteTreeReader extends LazyTreeReader {

  private RunLengthByteReader reader = null;

  public LazyByteTreeReader(int columnId, long rowIndexStride) {
    super(columnId, rowIndexStride);
  }

  @Override
  public void startStripe(Map<StreamName, InStream> streams, List<OrcProto.ColumnEncoding> encodings,
      RowIndex[] indexes, long rowBaseInStripe) throws IOException {
    super.startStripe(streams, encodings, indexes, rowBaseInStripe);
    reader = new RunLengthByteReader(streams.get(new StreamName(columnId,
        OrcProto.Stream.Kind.DATA)));
  }

  @Override
  public void seek(PositionProvider index) throws IOException {
    reader.seek(index);
  }

  @Override
  public Object next(Object previous) throws IOException {
    ByteWritable result = null;
    if (valuePresent) {
      if (previous == null) {
        result = new ByteWritable();
      } else {
        result = (ByteWritable) previous;
      }
      result.set(reader.next());
    }
    return result;
  }

  @Override
  public void skipRows(long numNonNullValues) throws IOException {
    reader.skip(numNonNullValues);
  }
}