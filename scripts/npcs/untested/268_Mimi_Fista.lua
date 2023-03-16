local npc = Npc(268, 71)

npc.gender = 1
npc.colors = {13493216, 1246464, 13764102}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1121)
    end
end

RegisterNPCDef(npc)
