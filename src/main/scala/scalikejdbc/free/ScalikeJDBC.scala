package scalikejdbc.free

import scalikejdbc.free.Query._
import scalikejdbc.{NoExtractor, WithExtractor, SQL, HasExtractor}

import scalaz.Free.FreeC
import scalaz.{Free, Inject}

sealed class ScalikeJDBC[F[_]](implicit I: Inject[Query, F]) {

  private def lift[A](v: Query[A]): FreeC[F, A] = Free.liftFC(I.inj(v))

  def seq[A](sql: SQL[A, HasExtractor]): FreeC[F, Seq[A]] = lift(GetSeq[A](sql.list()))
  def first[A](sql: SQL[A, HasExtractor]): FreeC[F, Option[A]] = lift(GetFirst[A](sql.first()))
  def single[A](sql: SQL[A, HasExtractor]): FreeC[F, Option[A]] = lift(GetSingle[A](sql.single()))

  def execute(sql: SQL[_, NoExtractor]): FreeC[F, Boolean] = lift(Execute(sql.execute()))
  def update(sql: SQL[_, NoExtractor]): FreeC[F, Int] = lift(Update(sql.update()))
  def generateKey(sql: SQL[_, NoExtractor]): FreeC[F, Long] = lift(UpdateWithGeneratedKey(sql.updateAndReturnGeneratedKey()))

}

object ScalikeJDBC {
  implicit def instance[F[_]](implicit I: Inject[Query, F]): ScalikeJDBC[F] = new ScalikeJDBC[F]
}