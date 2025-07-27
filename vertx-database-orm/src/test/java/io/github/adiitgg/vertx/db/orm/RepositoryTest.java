package io.github.adiitgg.vertx.db.orm;

import io.github.adiitgg.vertx.db.orm.annotation.Entity;
import io.github.adiitgg.vertx.db.orm.annotation.Id;
import io.github.adiitgg.vertx.db.orm.annotation.UpdatedAt;
import io.github.adiitgg.vertx.db.orm.model.PgRepositoryOptions;
import io.github.adiitgg.vertx.db.orm.util.RowUtil;
import io.vertx.core.Future;
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
import java.util.Arrays;
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
      .enableModule(true)
      .build();
    this.pgRepository = PgRepository.create(options);
    setupStructure();
  }

  private void setupStructure() {
    val sql = """
      CREATE TABLE m_user (
        id serial PRIMARY KEY,
        value varchar(255),
        updated_at timestamptz,
        deleted_at timestamptz
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
    private OffsetDateTime deletedAt;

    public SaveTestEntity(int id, String value, OffsetDateTime updatedAt, OffsetDateTime deletedAt) {
      this.id = id;
      this.value = value;
      this.updatedAt = updatedAt;
      this.deletedAt = deletedAt;
    }

    public SaveTestEntity(int id, String value, OffsetDateTime deletedAt) {
      this(id, value, null, deletedAt);
    }

    public SaveTestEntity(int id, String value) {
      this(id, value, null, null);
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
  void insertDeletedUser_success() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "user_deleted", OffsetDateTime.now());
    val returnRow = await(pgRepository.insert(entity));
    val rows = await(pgRepository.preparedQuery("SELECT id, deleted_at FROM m_user WHERE value = $1").execute(Tuple.of(entity.value)));
    assertNull(returnRow);
    assertEquals(0, rows.size());
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
  void insertBatch_success() {
    val entities = new SaveTestEntity[] {
      new SaveTestEntity(random.nextInt(100, 1000), "insertBatch1"),
      new SaveTestEntity(random.nextInt(100, 1000), "insertBatch2"),
      new SaveTestEntity(random.nextInt(100, 1000), "insertBatch3")
    };
    await(pgRepository.insertBatch(Arrays.stream(entities).toList()));
    val rows = await(pgRepository.preparedQuery("SELECT id, value FROM m_user WHERE value IN ($1, $2, $3)")
      .execute(Tuple.of(entities[0].value, entities[1].value, entities[2].value)));
    assertEquals(3, rows.size());
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


  @Test
  void transactionRollbackUpdate_failed() {
    val entity = new SaveTestEntity(random.nextInt(100, 1000), "transaction");
    await(pgRepository.insert(entity));
    await(pgRepository.transaction(tx -> {
      entity.value = "transaction-rollback-update";
      return tx.update(entity).compose(v -> Future.failedFuture(new RuntimeException("failed")));
    }).recover(err -> Future.succeededFuture(null)));
    val user = await(pgRepository.preparedQuery("SELECT id FROM m_user WHERE value = $1").execute(Tuple.of("transaction")).map(RowUtil::firstOrNull));
    assertNotNull(user);
  }
}
