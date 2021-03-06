package com.cupcake.learning.exam.util;

import graphql.relay.ConnectionCursor;
import graphql.relay.DefaultConnectionCursor;
import graphql.relay.Edge;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class CursorUtil {
  public ConnectionCursor createCursorWith(UUID id) {
    return new DefaultConnectionCursor(
        Base64.getEncoder().encodeToString(id.toString().getBytes(StandardCharsets.UTF_8)));
  }

  public UUID decode(String cursor) {
    return UUID.fromString(new String(Base64.getDecoder().decode(cursor)));
  }

  public ConnectionCursor createCursorWith(OffsetDateTime offsetDateTime) {
    return new DefaultConnectionCursor(
            Base64.getEncoder().encodeToString(offsetDateTime.toString().getBytes(StandardCharsets.UTF_8)));
  }

  public OffsetDateTime decodeDateTimeCursor(String cursor) {
    return OffsetDateTime.parse(new String(Base64.getDecoder().decode(cursor)));
  }

  public ConnectionCursor createCursorWith(Integer integer) {
    return new DefaultConnectionCursor(
            Base64.getEncoder().encodeToString(integer.toString().getBytes(StandardCharsets.UTF_8)));
  }

  public Integer decodeIntegerCursor(String cursor) {
    return Integer.parseInt(new String(Base64.getDecoder().decode(cursor)));
  }

  public <T> ConnectionCursor getFirstCursorFrom(List<Edge<T>> edges) {
    return edges.isEmpty() ? null : edges.get(0).getCursor();
  }

  public <T> ConnectionCursor getLastCursorFrom(List<Edge<T>> edges) {
    return edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();
  }
}
