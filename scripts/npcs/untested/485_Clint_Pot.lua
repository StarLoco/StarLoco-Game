local npc = Npc(485, 30)

npc.colors = {9147136, 3499887, 13158601}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2241)
    end
end

RegisterNPCDef(npc)
