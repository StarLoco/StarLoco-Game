local npc = Npc(427, 111)

npc.gender = 1
npc.colors = {13238569, 788231, 13813938}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
    end
end

RegisterNPCDef(npc)
