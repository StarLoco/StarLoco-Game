local npc = Npc(191, 9069)

npc.gender = 1
npc.colors = {6684672, 2527841, 5465171}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1239)
    end
end

RegisterNPCDef(npc)
