local npc = Npc(839, 70)

npc.colors = {3616719, 6799305, 15263798}
npc.accessories = {0, 7150, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3458, {3059})
    elseif answer == 3059 then p:ask(3459, {3060, 3061})
    end
end

RegisterNPCDef(npc)