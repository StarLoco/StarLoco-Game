local map = MapDef(
	15203,
	"0706131721",
	"384838613756345c7b5d775f645c5b3d695857324d277140306b7454425a7129506d6c4f567d58614f435e2837587e456c5557763d2c2d7e5c7e7c2c657a4f707e2767722a54553759742f554a394e2674736b506d47554e4d7a4774556922497e2532352d39722f4c42487a2a557b4f52342532425973733e524c7b50505f6a52275a306779405a7e527f665977575d5e4f4a412458206b34262d7f30206c2e5f7c36705262532532357c204f202a5d293829613a43604e62776525324230285255233d63503838584e7b3131627175294f784234784a54557524552f7e775a",
	"GhaaeaaaaaGhaae6HaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaae6HaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaHhGae8raaaHhGaeaaa6IHhGaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaa6IHhGae6HaaaHhGaeaaaaaHhGaeaaaaaHhGae6HaaaHhGaeaaaaaHhGaeaaa6IHhaae6HaaaHhaaeJga7dGhaaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhaaeoIaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeoIaaaHhaaeaaaaaGhaaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa6IGhaaeaaa6FHhGaeaaaaaHhGaeaaaaaHhGae8raaaHhGaeaaaaaHhaaeaaaaaHhaae8raaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae6HaaaHhaaeaaaaaGhaaeaaa6GHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhaaeaaa6FHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeoIaaaHhaaeJgaaaGhaaeoIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGae8raaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaHhaae8ra7dHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IGhaaeaaaX4HhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaGhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa6LGhaaeaaaaaGhaaeaaa7aHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaae6HaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeoIaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaa6IGhaaeaaaaaGhaaeaaa6IGhaae6HaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaGhaae6Ha7eGhaaeaaaaaGhaaeJga6_HhGae8raaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhaae6HaaaGhaaeaaaaaHhaaeaaa6TGhaaeaaaaaHhGaeaaaaaGhaaeaaaaaGhaaeaaa6",
	15,
	17,
	1,
	0,
	445
)

map.positions = "dJd0eme7fifHfNg6g9ha|dedjebeUe4fMgWgXg5hk"
map.capabilities = 78
map.mobGroupsCount = 3
map.mobGroupsMaxSize = 5
map.npcs = {
	[867] = {210, 1},
}
map.allowedMobGrades = {
	{972, 1},
	{972, 2},
	{972, 3},
	{972, 4},
	{972, 5},
	{971, 1},
	{971, 2},
	{971, 3},
	{971, 4},
	{971, 5},
	{973, 1},
	{973, 2},
	{973, 3},
	{973, 4},
	{973, 5},
	{984, 1},
	{984, 2},
	{984, 3},
	{984, 4},
	{984, 5},
}
-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[260] = moveEndTeleport(10278, 233),
	[460] = moveEndTeleport(10286, 40),
}

