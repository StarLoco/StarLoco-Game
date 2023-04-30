local npc = Npc(781, 91)

npc.gender = 1
npc.accessories = {0, 0x1fb4, 0x1fb5, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    ScaraleafDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)
