local npc = Npc(1091, 51)

npc.gender = 1
npc.colors = {16082697, 16082697, 1493192}
npc.accessories = {0, 9663, 9180, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6215)
    end
end

RegisterNPCDef(npc)
