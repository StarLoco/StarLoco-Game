local map = MapDef(
	6226,
	"0706131721",
	"562A58295A6D5B3341524540345B303F4449746175552C57715730364F5E5260793F7B48513C6F723741552121386B2E5F494D52483C644D353A236F51464124787931325F6328376E697860517C45743B6C602A4F5C44443B5B415A344A412C5A36372F3A782A432624412E667B6E35463D2133662938225F6874742532624D516E2A6F7C7E27665F4C6330564730564F22744727755471574A552E783E6B2752284941307C534E23253235244A526A7A796C62357C443C513B474D265E3F777965714B49262D5F6B773324354D4B797C7C2A324E654B6D2532357A2E54266C4744435C5C7C305A40",
	"bhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaabhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeofaaaHhaaeofiaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeofaaaHhbceaaaoiHhaaeofiaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeofaaaHhrceqgaaaHhbceaaaqXHhaaeofiaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeofaaaHhHceaaaaaHhHceaaaaaHhHceaaaaaHhaaeofiaaHhaaeaaaaaHhaaeaaaaaHhaaeofaaaGhbceaaaxOHhHceaaaaaHhHcem4aaaHhbceaaexKHhaaeofiaaHhaaeaaaaaHhaaeaaaaaHhbceoIaaaHhHceoIaaaHhHceaaaaaHhHceaaaaaHhbceaaaaaHhaaeofiaaHhaaeaaaaaHhcoeaaaaaHhjceaadY2HhHcem4aaaHhHceaaaaaHhHceaaaaaGhbceaaaaaHhaaeofiaaHhaaeaaaaaHhcoeaaaaaHhbceaaaaaHhjceoIdTOHhHceqYaaaHhbceaaaxGGhbceaaaaaHhaaeofiaabhGaeaaaaaHhcoeaaaaaHhbceaaaaaHhHceaaaaaHhbceoJaaaHhbceaaaaaHhbceaaaxPHhaaeaaaaabhGaeaaaaaHhcoeaaaaaHhHceaaaaaHhHceaaaaaHhbceaaaaaHhHceaaaaaHhcpeaaaaabhGaeaaaaabhGaeaaaaaHhcoeaaaaaHhHceaaaaaHhHceh2aaaHhHceaaaaaHhcpeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhcoeaaaaaHhHceaaaaaHhHceaaaaaHhcpeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhcoeaaaaaHhHceaaaaaHhcpeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhcoeaaaaaHhcpeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaaHhaaeaaaaabhGaeaaaaabhGaeaaaaabhGaeaaaaa",
	8,
	13,
	-24,
	35,
	53
)

map.positions = "bSb0b6b7b8cdckcr|cscuczcAcBcHcIcP"
map.mobGroupsCount = 3
map.mobGroupsMinSize = 8

-- '0;0;0;0;0;0;0' forbiddens -> capabilities ? Or script ?

map.onMovementEnd = {
	[92] = moveEndTeleport(6223, 86),
}

