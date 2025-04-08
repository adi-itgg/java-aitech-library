package io.aitech.vertx.db.orm;

import io.aitech.vertx.db.orm.annotation.Entity;
import io.aitech.vertx.db.orm.annotation.Id;
import io.aitech.vertx.db.orm.annotation.UpdatedAt;
import io.aitech.vertx.db.orm.model.PgRepositoryOptions;
import io.aitech.vertx.db.orm.util.RowUtil;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Accessors(fluent = true)
@ExtendWith(VertxExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryTest extends BaseEmbeddedTest {

  private PgRepository pgRepository;
  private static final Random random = new Random();

  @Override
  protected void onStart(Vertx vertx, DatabaseOptions databaseOptions) {
    val pgConnectOptions = new PgConnectOptions()
      .setPort(databaseOptions.port())
      .setDatabase(databaseOptions.database())
      .setUser(databaseOptions.user())
      .setPassword(databaseOptions.password());
    val options = PgRepositoryOptions.newBuilder()
      .pool(Pool.pool(vertx, pgConnectOptions, new PoolOptions().setMaxSize(4)))
      .debug(true)
      .build();
    this.pgRepository = PgRepository.create(options);
    setupStructure();
  }

  private void setupStructure() {
    val sql = """
      CREATE TABLE m_user (
        id serial PRIMARY KEY,
        value varchar(255),
        updated_at timestamptz
      );
      """;
    await(pgRepository.preparedQuery(sql).execute());
  }

  @Entity(name = "m_user")
  public static final class SaveTestEntity {

    @Id
    private Integer id;
    private String value;
    private @UpdatedAt OffsetDateTime updatedAt;

    public SaveTestEntity(int id, String value) {
      this.id = id;
      this.value = value;
    }

  }


  @Test
  void insert_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "test123");
    val returnRow = await(pgRepository.insert(entity));
    val rows = await(pgRepository.preparedQuery("SELECT id FROM m_user WHERE value = $1").execute(Tuple.of(entity.value)));
    assertNull(returnRow);
    assertEquals(1, rows.size());
  }

  @Test
  void insertReturning_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "insert_returning");
    val row = await(pgRepository.insert(entity, true));
    assertNotNull(row);
    assertEquals(entity.value, row.getValue("value"));
  }


  @Test
  void insertEntity_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "insertEntity");
    val currentEntity = await(pgRepository.insertEntity(entity));
    assertNotNull(currentEntity);
    assertEquals(entity.id, currentEntity.id);
  }

  @Test
  void insertEntityUpdate_success() {
    val entity = new SaveTestEntity(-1, "insertEntityUpdate");
    val currentEntity = await(pgRepository.insertEntity(entity, true));
    assertNotNull(currentEntity);
    assertNotEquals(-1, entity.id);
    assertNotEquals(-1, currentEntity.id);
  }

  @Test
  void update_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "update");

    await(pgRepository.insertEntity(entity, true));

    entity.value = "update-now";

    await(pgRepository.update(entity));
    val row = await(pgRepository.preparedQuery("SELECT value FROM m_user WHERE id = $1").execute(Tuple.of(entity.id)).map(RowUtil::firstOrNull));
    assertNotNull(row);
    assertEquals(entity.value, row.getValue(0));
  }

  @Test
  void updateReturning_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "updateReturning");

    await(pgRepository.insertEntity(entity, true));

    entity.value = "update-returning";

    val returnRow = await(pgRepository.update(entity, true));
    val row = await(pgRepository.preparedQuery("SELECT value FROM m_user WHERE id = $1").execute(Tuple.of(entity.id)).map(RowUtil::firstOrNull));
    assertNotNull(returnRow);
    assertNotNull(row);
    assertEquals(entity.value, row.getValue(0));
    assertEquals(entity.value, returnRow.getValue("value"));
  }


  @Test
  void updateEntity_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "updateEntity");

    await(pgRepository.insertEntity(entity, true));

    entity.value = "update-entity";

    await(pgRepository.updateEntity(entity));
    val row = await(pgRepository.preparedQuery("SELECT value FROM m_user WHERE id = $1").execute(Tuple.of(entity.id)).map(RowUtil::firstOrNull));
    assertNotNull(row);
    assertEquals(entity.value, row.getValue(0));
  }

  @Test
  void updateEntityReturning_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "updateEntityReturning");

    await(pgRepository.insertEntity(entity, true));

    entity.value = "update-entity-returning";

    val currentEntity = await(pgRepository.updateEntity(entity, true));
    val row = await(pgRepository.preparedQuery("SELECT value FROM m_user WHERE id = $1").execute(Tuple.of(entity.id)).map(RowUtil::firstOrNull));
    assertNotNull(currentEntity);
    assertNotNull(row);
    assertEquals(entity.value, row.getValue(0));
    assertEquals(entity.value, currentEntity.value);
  }

  @Test
  void updateSpecifictColumn_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "updateEntityReturning");

    await(pgRepository.insertEntity(entity, true));

    entity.value = "update-entity-returning";

    await(pgRepository.update(entity, (e) -> entity.value = "updateReturningSpecifictCol"));
    val row = await(pgRepository.preparedQuery("SELECT value FROM m_user WHERE id = $1").execute(Tuple.of(entity.id)).map(RowUtil::firstOrNull));
    assertNotNull(row);
    assertEquals(entity.value, row.getValue(0));
  }

}
