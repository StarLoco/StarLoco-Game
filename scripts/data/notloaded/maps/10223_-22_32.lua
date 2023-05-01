local map = MapDef(
	10223,
	"0706131721",
	"5462362127524E28467562707C7D3B5362536C587C35512E7A6C7D5A572C262532624355284F3465384F483F44315A412C7C35717A20384B327D65645A46326F253262742740592155714E3764384F293F547575774169507B3B45792067545C4C6C784825326259306A484E2532353B4B78492728246667405E3221412C42235470672237654A753C4955402F263866443F735778787023652F543A6F2A3B613E31265A43657E34743B3D44654171223D71285A4A264F2532625879387E635E5A263134204E706F58",
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
map.mobGroupsSize = 6
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
