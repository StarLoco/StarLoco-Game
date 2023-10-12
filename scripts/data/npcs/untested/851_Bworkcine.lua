local npc = Npc(851, 1241)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3618)
    end
end

RegisterNPCDef(npc)
