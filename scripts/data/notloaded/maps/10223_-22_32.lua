local map = MapDef(
	10223,
	"0706131721",
	"5462362127524e28467562707c7d3b5362536c587c35512e7a6c7d5a572c262532424355284f3465384f483f44315a412c7c35717a20384b327d65645a46326f253242742740592155714e3764384f293f547575774169507b3b45792067545c4c6c784825324259306a484e2532353b4b78492728246667405e3221412c42235470672237654a753c4955402f263866443f735778787023652f543a6f2a3b613e31265a43657e34743b3d44654171223d71285a4a264f2532425879387e635e5a263134204e706f58",
	"HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeytaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaeaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeytaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhqaeoIaqgHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeytaaaHhGaeaaaaaHhGaeIyaaaHhaaeA4eaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeIyaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeytaaaHhGaeaaaaaHhGaeoIaaaHhGaeaaeaaHhaaeIyaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeIyaaaHhGaeaaaaaHhGaeoIaaaHhaaeaaaaaHhaaeytaaaGhaaeoIayOHhGaeIyaaaHhGaeA4aaaHhGaeaaaaaHhaaeaaaaaHhaaeaaiaaHhGaeaaaaaHhGaeIyaaaHhGaeaaaaaHhGaeIyaaaHhGaeaaaaaHhaaeytaaaHhGaeaaaA6HhGaeIyaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeoIaaaHhGaeaaaaaHhGaeIyaaaHhGaeaaaaaHhGaeaaiaaGhaaeaaaA5HhaaeoIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaiaaHhaaeaaaaaHhaaeaaaA5HhGaeIyaaaHhGaeA4aaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaA6HhaaeIyaA6HhGaeA4aaaHhGaeIyeaaHhGaeaaaaaHhGaeoIaaaGhaaeaaaaaHhaaeaaaaaHhqaeA4aqgHhGaeIyaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaAWGhaaeaaaA3HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaA6GhaaeaaaaaHhaaeaaaaa",
	7,
	9,
	-22,
	32,
	75
)

map.positions = "aKaQaRaXaYa4a5a_babg|a8bcbdbjbkbqbrbxbybE"
map.capabilities = 36
map.mobGroupsCount = 3
map.mobGroupsMaxSize = 6
map.allowedMobGrades = {
	{442, 1},
	{442, 2},
	{442, 3},
	{442, 4},
	{442, 5},
	{447, 1},
	{447, 2},
	{447, 3},
	{447, 4},
	{447, 5},
	{449, 1},
	{449, 2},
	{449, 3},
	{449, 4},
	{449, 5},
	{112, 1},
	{112, 2},
	{112, 3},
	{112, 4},
	{112, 5},
	{259, 1},
	{259, 2},
	{259, 3},
	{259, 4},
	{259, 5},
}
-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[25] = moveEndTeleport(10225, 436),
	[98] = moveEndTeleport(6744, 330),
}

RegisterMapDef(map)
