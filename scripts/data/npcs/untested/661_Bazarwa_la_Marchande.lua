local npc = Npc(661, 121)

npc.gender = 1
npc.colors = {13603034, 9978301, 6906601}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2846)
    end
end

RegisterNPCDef(npc)
