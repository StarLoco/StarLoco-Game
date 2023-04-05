local npc = Npc(779, 30)

npc.accessories = {0, 0x1b28, 0x1b2a, 0, 0}

---@param p Player
function npc:onTalk(p, answer)
    CanidaeDungeon:onTalkToGateKeeper(p, answer)
end

RegisterNPCDef(npc)