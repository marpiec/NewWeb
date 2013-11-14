/**
 *
 */
case class XY(val x: Int, val y: Int)

case class JoinAGameMessage(ships: List[XY])

case class UserJoinedGame(gameId: Int, playerId: Int)