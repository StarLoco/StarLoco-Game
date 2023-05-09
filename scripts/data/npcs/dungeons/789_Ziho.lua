local npc = Npc(789, 70)

npc.accessories = {0, 8287, 8286, 0, 0}

local sanctuaryKeyId = 8342

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    local hasDungeonKey = p:getItem(sanctuaryKeyId) or hasKeyChainFor(p, sanctuaryKeyId)
    local showKeyResponse = 2876

    if answer == 0 then
        local responses = hasDungeonKey and {showKeyResponse, 2877, 2878} or {2877, 2878}
        p:ask(3236, responses)
    elseif answer == 2877 then p:ask(3237, {2882})
    elseif answer == 2882 then p:ask(3242, {2883})
    elseif answer == 2883 then p:ask(3242, {2887})
    elseif answer == 2887 then p:ask(3247, {2888})
    elseif answer == 2888 then p:ask(3248, {2889})
    elseif answer == 2889 then p:ask(3249, {2890})
    elseif answer == 2890 then p:ask(3250, {2891})
    elseif answer == 2891 then p:ask(3251, {2892})
    elseif answer == 2892 then p:ask(3252)
    elseif answer == 2878 then p:ask(3238, {2879})
    elseif answer == 2879 then p:ask(3238, {2880})
    elseif answer == 2880 then p:ask(3240, {2881})
    elseif answer == 2881 then p:ask(3241)
    elseif answer == 2876 then
        p:teleport(9822, 89)
        p:endDialog()
    end
end

RegisterNPCDef(npc)
