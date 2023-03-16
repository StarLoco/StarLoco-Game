local npc = Npc(765, 9079)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3150, {2772, 2771}, "[name]")
    elseif answer == 2772 then p:ask(3152)
    end
end

RegisterNPCDef(npc)
