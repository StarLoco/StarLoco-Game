local npc = Npc(735, 51)

npc.gender = 1
npc.colors = {16777215, 1384400, 732944}
npc.accessories = {0, 702, 0, 0, 7087}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3030, {2673})
    elseif answer == 2673 then p:ask(3031)
    end
end

RegisterNPCDef(npc)