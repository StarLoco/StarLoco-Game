local npc = Npc(119, 120)

npc.accessories = {0, 8208, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(386, {310}, "[name]")
    elseif answer == 310 then p:ask(387)
    end
end

RegisterNPCDef(npc)